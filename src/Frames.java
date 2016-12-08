import javax.swing.JFrame;

public class Frames implements Runnable{

    JFrame theFrame;


    public Frames(JFrame f) {
        this.theFrame = f;
    }


    // Run two Frames in separate therads
    public static void main(String[] arguments) {
        JFrame f1 = new JFrame("f1 title");
        
        Thread t1 = new Thread(new Frames(f1));
        
        t1.start();
        
    }


    @Override
    public void run() {
        theFrame.setSize(200, 200);
        theFrame.setVisible(true);

        // Attention: This closes the app, and therefore both frames!
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Thread t= new Thread(){
        
        	public void run(){
        		 JFrame f1 = new JFrame("f2 title");
        		 f1.setSize(200, 200);
        	        f1.setVisible(true);

        	}
        };
        
        t.start();
        
    }

}
