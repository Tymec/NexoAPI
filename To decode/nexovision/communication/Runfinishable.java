package eu.nexwell.android.nexovision.communication;

public abstract class Runfinishable implements Runnable {
    private boolean finish = false;

    public void finish() {
        this.finish = true;
    }

    protected boolean isFinished() {
        return this.finish;
    }

    protected boolean sleepAndCheckIfIsFinished(int timems) {
        if (isFinished()) {
            return true;
        }
        for (int i = 0; i < timems / 10; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isFinished()) {
                return true;
            }
        }
        return false;
    }
}
