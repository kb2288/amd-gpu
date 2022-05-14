package hp.community.gpu.app.service;

public interface GpuTask {

    /**
     * Used to start the GPU task.
     */
    void start();

    /**
     * Used to stop the GPU task.
     */
    void stop();

    /**
     * Receive the task name
     */
    String getTaskName();
}
