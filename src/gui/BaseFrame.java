package gui;
import databaseObjects.Users;
import javax.swing.*;

public class BaseFrame extends JFrame{
    protected Users user;

    public BaseFrame(String title, Users user){
        this.user= user;
        initialize(title);
    }

    public BaseFrame(String title){
        initialize(title);
    }

    private void initialize(String title){
        setTitle(title);
        setSize(420,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        addGuiComponents();
    }

    protected abstract void addGuiComponents();
}
