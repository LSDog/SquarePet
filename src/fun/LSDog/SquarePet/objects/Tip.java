package fun.LSDog.SquarePet.objects;

import javax.swing.*;
import java.awt.*;

public class Tip extends JFrame {

    public JLabel label;

    public Component parent;
    private int relativeX = 0;
    private int relativeY = 0;

    private boolean htmlEnable = true;
    private long textId = 0;
    private final AutoRemove autoRemove = new AutoRemove();

    public Tip() {
        setUndecorated(true);
        setAlwaysOnTop(true);
        setType(Type.UTILITY); // 隐藏任务栏图标
        getContentPane().setBackground(new Color(0,0,0,0));
        setBackground(new Color(0,0,0,0));
        label = new JLabel();
        label.setForeground(new Color(0xD06F07));
        label.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        setHtmlEnable(false);
        add(label);
        setVisible(true);
        new Thread(autoRemove).start();
    }

    public void setHtmlEnable(boolean htmlEnable) {
        Boolean disable = htmlEnable ? Boolean.FALSE : Boolean.TRUE;
        if (htmlEnable != this.htmlEnable) {
            label.putClientProperty("html.disable", disable);
            this.htmlEnable = htmlEnable;
        }
    }

    public void showHtmlText(String html, long hideMs) {
        setHtmlEnable(true);
        showText(html, hideMs);
        setHtmlEnable(false);
    }

    public void showText(String text, long hideMs) {
        long id = ++textId;
        label.setText(text);
        pack();
        if (hideMs > 0) {
            autoRemove.addRequest(id, hideMs);
        }
    }

    public void showText(String text) {
        showText(text, 0);
    }

    public void hideText() {
        setHtmlEnable(false);
        label.setText("");
    }

    private class AutoRemove implements Runnable {

        private AutoRemove() {
        }

        private boolean removed = true;
        private long removeId = -1;
        private long hideStamp;

        private synchronized void addRequest(long textId, long hideMs) {
            removeId = textId;
            hideStamp = System.currentTimeMillis() + hideMs;
            removed = false;
        }

        @Override
        @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
        public void run() {
            try {
                while (true) {
                    if (!removed && removeId != -1) {
                        if (removeId != Tip.this.textId) {
                            removed = true;
                        } else if (System.currentTimeMillis() >= hideStamp) {
                            hideText();
                            removed = true;
                        }
                    }
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRelative(int relativeX, int relativeY) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    public void updateLocation() {
        if (parent == null) return;
        setLocation(parent.getX() + relativeX, parent.getY() + relativeY);
    }
}
