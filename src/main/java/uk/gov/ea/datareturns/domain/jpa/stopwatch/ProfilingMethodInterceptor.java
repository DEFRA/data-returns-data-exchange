package uk.gov.ea.datareturns.domain.jpa.stopwatch;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class ProfilingMethodInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ProfilingMethodInterceptor.class);

    public Object invoke(MethodInvocation invocation) throws Throwable {

        if (log.isInfoEnabled()) {
            String stopWatchName = invocation.getMethod().toGenericString();
            StopWatchHierarchy stopWatch = StopWatchHierarchy.getStopWatchHierarchy(stopWatchName);

            String taskName = invocation.getMethod().getName();
            stopWatch.start(taskName);

            try {
                return invocation.proceed();
            } finally {
                stopWatch.stop();
            }
        } else {
            return invocation.proceed();
        }
    }

    static class StopWatchHierarchy {
        private static final ThreadLocal<StopWatchHierarchy> stopwatchLocal = new ThreadLocal<StopWatchHierarchy>();
        private static final IndentStack indentStack = new IndentStack();

        static StopWatchHierarchy getStopWatchHierarchy(String id) {

            StopWatchHierarchy stopWatch = stopwatchLocal.get();
            if (stopWatch == null) {
                stopWatch = new StopWatchHierarchy(id);
                stopwatchLocal.set(stopWatch);
            }
            return stopWatch;
        }

        static void reset() {
            stopwatchLocal.set(null);
        }

        final StopWatch stopWatch;
        final Stack stack;

        StopWatchHierarchy(String id) {
            stopWatch = new StopWatch(id);
            stack = new Stack();
        }

        void start(String taskName) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            taskName = indentStack.get(stack.size) + taskName;
            stack.push(taskName);
            stopWatch.start(taskName);
        }

        void stop() {
            stopWatch.stop();
            stack.pop();
            if (stack.isEmpty()) {
                log.info(stopWatch.prettyPrint());
                StopWatchHierarchy.reset();
            } else {
                stopWatch.start(stack.get());
            }
        }

    }

    static class Stack {
        private int size = 0;
        String elements[];

        public Stack() {
            elements = new String[10];
        }

        public void push(String e) {
            if (size == elements.length) {
                ensureCapa();
            }
            elements[size++] = e;
        }

        public String pop() {
            String e = elements[--size];
            elements[size] = null;
            return e;
        }

        public String get() {
            return elements[size - 1];
        }

        public boolean isEmpty() {
            return size == 0;
        }

        private void ensureCapa() {
            int newSize = elements.length * 2;
            elements = Arrays.copyOf(elements, newSize);
        }
    }

    static class IndentStack {
        String elements[] = new String[0];

        public String get(int index) {
            if (index >= elements.length) {
                ensureCapa(index + 10);
            }
            return elements[index];
        }

        private void ensureCapa(int newSize) {
            int oldSize = elements.length;
            elements = Arrays.copyOf(elements, newSize);
            for (int i = oldSize; i < elements.length; i++) {
                elements[i] = new String(new char[i]).replace("\0", "|   ");
            }
        }
    }
}