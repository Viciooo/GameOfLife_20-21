package backend;

public class Testing implements Runnable {
    private String message;

    public Testing(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        for(int i = 0;i< 20;i++){
            System.out.println(message);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
