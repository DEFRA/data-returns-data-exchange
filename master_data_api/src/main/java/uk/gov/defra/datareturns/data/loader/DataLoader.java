package uk.gov.defra.datareturns.data.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;
import uk.gov.defra.datareturns.config.SecurityConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Database loader functionality to load baseline data into the master data API
 *
 * @author Sam Gardner-Dell
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DataLoader {
    private final Map<String, DatabaseLoader> loaderBeans;

    /**
     * Invoke all database loaders with respect to their dependency tree
     */
    public void loadAll() {
        final List<DatabaseLoader> loaders = resolveLoadOrder();
        SecurityConfiguration.runAsSystemUser(() -> loaders.forEach(DatabaseLoader::load));
    }

    /**
     * Retrieve a {@link List} of {@link DatabaseLoader} instances in the order they should be loaded (via {@link DatabaseLoader}.dependsOn()
     *
     * @return the {@link List} of {@link DatabaseLoader}s in dependency order
     */
    private List<DatabaseLoader> resolveLoadOrder() {
        final Map<Class<?>, DatabaseLoader> loadersByClass = loaderBeans.values().stream()
                .collect(Collectors.toMap(AopUtils::getTargetClass, Function.identity()));
        final DefaultDirectedGraph<Class<?>, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        loadersByClass.keySet().forEach(graph::addVertex);
        loadersByClass.forEach((cls, lb) -> lb.dependsOn().forEach(dep -> graph.addEdge(dep, cls)));
        final CycleDetector<Class<?>, DefaultEdge> detector = new CycleDetector<>(graph);
        final Set<Class<?>> cycles = detector.findCycles();
        if (cycles.isEmpty()) {
            final TopologicalOrderIterator<Class<?>, DefaultEdge> iter = new TopologicalOrderIterator<>(graph);
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED), false)
                    .map(loadersByClass::get)
                    .collect(Collectors.toList());
        } else {
            final String msg = "Database loaders contain cyclic dependencies " + cycles;
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }
}
