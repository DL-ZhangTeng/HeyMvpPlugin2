package com.github.duoluo9.HeyMVPPlugin;

import javax.swing.*;
import java.awt.event.*;

public class CreateDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private javax.swing.JLabel JLabel;

    private DialogCallback mCallback;

    public CreateDialog(DialogCallback callback) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(300, 150);
        setLocationRelativeTo(null);
        mCallback=callback;
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if (mCallback!=null){
            mCallback.ok(textField1.getText().trim());
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


    public interface DialogCallback{
        void ok(String name);
    }
}
