package eu.nexwell.android.nexovision.communication;

import android.util.Log;

public class ThreadManager {
    private String name = null;
    private Runfinishable runfinishable = null;
    private Thread thread = null;

    public ThreadManager(String name) {
        Log.d("ThreadManager", "Create " + name);
        this.name = name;
    }

    public void restart(Runfinishable runfinishable) {
        stop();
        this.runfinishable = runfinishable;
        Log.d("NexoTalk", this.name + ".restart(): creating");
        this.thread = new Thread(runfinishable);
        Log.d("NexoTalk", this.name + ".restart(): start");
        this.thread.start();
    }

    public void stop() {
        if (this.thread == null) {
            Log.d("NexoTalk", this.name + ".stop(): already null");
        } else if (this.thread.isAlive()) {
            Log.d("NexoTalk", this.name + ".stop(): finishing...");
            finish();
        } else {
            Log.d("NexoTalk", this.name + ".stop(): already dead");
        }
        this.thread = null;
        this.runfinishable = null;
    }

    public boolean isRunning() {
        return this.runfinishable != null;
    }

    public Runfinishable getRunfinishable() {
        return this.runfinishable;
    }

    private void finish() {
        this.runfinishable.finish();
        try {
            Log.d("NexoTalk", this.name + ".finish(): waiting for join...");
            this.thread.join();
            Log.d("NexoTalk", this.name + "finish(): joined");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
