package fun.LSDog.SquarePet.objects;

import fun.LSDog.SquarePet.animations.FadeOpacity;
import fun.LSDog.SquarePet.enums.Sounds;
import fun.LSDog.SquarePet.utils.FileUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Square extends JFrame {

    private final double fps;

    private Point relativeMouseLoc;

    private boolean dragging = false;
    private double dx = 0;
    private double dy = 0;
    private double friction; // 平面摩擦力
    private double gravity; // 重力
    private Integer prevYHit;
    private Integer prevXHit;

    public final Tip tip;
    private final JFrame face;
    private boolean faceShown;
    private int faceShowFrames = 0;
    private final Dimension screen = getToolkit().getScreenSize();

    private final Queue<Long> cpsQueue = new ConcurrentLinkedQueue<>();
    private int prevCps = 0;

    public Square(double fps, double friction, double gravity) {

        this.fps = fps;
        this.friction = friction;
        this.gravity = gravity;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(64, 64);
        setLocationRelativeTo(null); // 居中
        setUndecorated(true); // 没有边框
        setAlwaysOnTop(true); // 置顶
        getContentPane().setBackground(new Color(0,0,0,0)); // ContentPane 罩在 frame 本体上了...
        setBackground(new Color(0xBB307070, true));
        Border border = BorderFactory.createLineBorder(new Color(0xFFCC00), 5);
        getRootPane().setBorder(border);
        setFont(new Font("黑体", Font.PLAIN, 40));

        tip = new Tip();
        tip.setParent(this);
        tip.setRelative(70, -20);
        tip.updateLocation();

        face = new JFrame() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                try {
                    Graphics graphics = getContentPane().getGraphics();
                    BufferedImage image = ImageIO.read(FileUtil.getResource("res/layer-shy.png"));
                    graphics.drawImage(image, 0, 0, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        face.setSize(64, 64);
        face.setLocation(getX(), getY());
        face.setUndecorated(true); // 没有边框
        face.setAlwaysOnTop(true); // 置顶
        face.setType(Type.UTILITY);
        face.setBackground(new Color(0,0,0,0));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 27) System.exit(0);
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) setFriction(Square.this.friction + 0.1);
                if (e.getKeyCode() == KeyEvent.VK_LEFT) setFriction(Square.this.friction - 0.1);
                if (e.getKeyCode() == KeyEvent.VK_UP) setGravity(Square.this.gravity + 0.1);
                if (e.getKeyCode() == KeyEvent.VK_DOWN) setGravity(Square.this.gravity - 0.1);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cpsQueue.offer(e.getWhen());
                if (relativeMouseLoc == null) relativeMouseLoc = e.getPoint();
                relativeMouseLoc.setLocation(e.getX(), e.getY());
                // 这里的位置是光标在窗口的相对坐标
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                dragging = true;
                tip.updateLocation();
                int x = e.getXOnScreen() - relativeMouseLoc.x;
                int y = e.getYOnScreen() - relativeMouseLoc.y;
                x -= getXtoEdge(x);
                y -= getYtoEdge(y);
                dx = x - getX();
                dy = y - getY();
                setLocation(x, y);
            }
        });

        Timer cpsCounter = new Timer();
        cpsCounter.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long previousSec = System.currentTimeMillis() - 1000;
                for (Long t : cpsQueue) {
                    if (t <= previousSec) {
                        cpsQueue.poll();
                    } else break;
                }
            }
        }, 0, 100);

        startLoop(fps);
    }

    public void setFriction(double friction) {
        this.friction = friction;
        tip.showText("f = " + String.format("%.1f", friction), 750);
    }

    public boolean haveGravity() {
        // 精度丢失问题 (0.4-0.1 != 0.3)
        return gravity < -0.01 || 0.01 < gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
        tip.showText("g = " + String.format("%.1f", gravity), 750);
    }

    public int getCps() {
        return cpsQueue.size();
    }


    private void step() {

        // 物理计算
        if (!dragging) {
            if (haveGravity() || dx != 0 || dy != 0) {

                tip.updateLocation();

                double _dx = dx;
                double _dy = dy;

                if (!haveGravity()) {
                    // 平面 摩擦
                    double r = Math.sqrt(dx*dx+dy*dy);
                    double fx = friction * dx / r;
                    double fy = friction * dy / r;
                    _dx -= (Math.abs(fx) < Math.abs(dx) ? fx : dx); // 摩擦力比移动距离还大就让他减去自己 得零
                    _dy -= (Math.abs(fy) < Math.abs(dy) ? fy : dy);
                } else {
                    _dy += gravity;
                }

                _2Elems<Integer, Integer> crash = getEdgeHit(_dx, _dy); // 算下有没有碰撞什么的...

                if ((prevYHit == null && crash.e2 != null) || (prevXHit == null && crash.e1 != null)) { // 假如上次没撞过
                    //new Thread(Sounds.HIT::play).start();
                    /* 3D音效lol */
                    float halfScreen = ((float) screen.getWidth()) / 2;
                    Sounds.HIT.getSound().setPan((getX()-halfScreen)/halfScreen/1.5f);
                    Sounds.HIT.play();
                }

                prevXHit = crash.e1;
                prevYHit = crash.e2;

                if (crash.e1 != null) _dx = -_dx; // 碰撞反转方向
                if (crash.e2 != null) _dy = -_dy;

                if (haveGravity()) {
                    double friDouble = friction * 2; // 为了明显一点动能损失大一些
                    // 重力 摩擦值变为动能损失
                    if (crash.e1 != null) {
                        if (_dx > 0) _dx = Math.abs(_dx - Math.min(friDouble, _dx));
                        else if (_dx < 0) _dx = - Math.abs(_dx + Math.max(friDouble, _dx));
                    }
                    if (crash.e2 != null) {
                        _dy += gravity; // 机械能守恒
                        double friGround = friction / 4 * Math.abs(gravity); // 地面摩擦
                        _dx -= _dx > 0 ? friGround : _dx < 0 ? - friGround : 0;
                        if (_dy > 0) _dy = Math.abs(_dy - Math.min(friDouble, _dy));
                        else if (_dy < 0) _dy = - Math.abs(_dy + Math.max(friDouble, _dy));
                    }
                }

                setLocation((int) Math.round(getX() + _dx), (int) Math.round(getY() + _dy));

                dx = _dx; // 实际值 = 预算值
                dy = _dy;

            }
        }

        // CPS 检测
        int cps = getCps();
        if (cps > 1 || prevCps > 0) {
            tip.showText("cps = " + cps, 250);
            prevCps = cps;
        }

        // 脸红
        if (cps >= 4 && !faceShown) faceShowFrames++;
        if (faceShowFrames/fps >= 2.5 && !faceShown) {
            System.out.println("show face");
            faceShowFrames *= 2;
            face.setVisible(true);
            setAlwaysOnTop(false);
            faceShown = true;
            FadeOpacity.fade(face, 0, 1, 10, 50L, null);
        } else if (faceShown && faceShowFrames != -1) {
            faceShowFrames--;
            if (faceShowFrames == 0) {
                System.out.println("hide face");
                // faceShown = false;
                // 别让他们再玩第二遍了
                FadeOpacity.fade(face, 1, 0, 10, 25L, _face -> {
                    _face.setVisible(false);
                    setAlwaysOnTop(true);
                    faceShowFrames = -1;
                });
            }
        }
        face.setLocation(getX(), getY());
    }

    @SuppressWarnings("BusyWait")
    private void startLoop(double fps) {
        new Thread(() -> {
            try {
                int frame = 0;
                double spf = 1D/fps; // 60 FPS
                double nspf = spf * 1000_000_000; // 纳秒每帧
                long preFrameNs = 0; // 上一帧 ms
                long freeNs; // 距离下一帧的空闲时间 ns
                while (true) {
                    long stepStartNs = System.nanoTime();
                    if (stepStartNs - preFrameNs >= nspf) {

                        step(); // 游戏逻辑~

                        if (frame == fps-1) frame = 0; else frame++; // 记录frame (一秒内的第几帧)
                        freeNs = (long) Math.floor(nspf - (System.nanoTime() - stepStartNs)); // 空闲时间
                        if (freeNs < 0) { // 如果空闲时间是负数、意味着执行时间已经大于 1/60 秒
                            if (preFrameNs != 0) System.err.println("LAG!! >> 执行时间大于 1/"+fps+"s.");
                            preFrameNs = 0;
                            continue; // 直接下一步
                        }
                        preFrameNs = stepStartNs;
                        Thread.sleep(freeNs/1000_000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 检测 x坐标 超出屏幕距离
     * @param x 需要检测的 x
     * @return int < 0 为超出屏幕左侧, int > 0 为超出右侧
     */
    private int getXtoEdge(int x) {
        if (x <= 0) return x;
        int overX = x - getToolkit().getScreenSize().width+getWidth();
        return Math.max(overX, 0);
    }

    /**
     * 检测 y坐标 超出屏幕距离
     * @param y 需要检测的 y
     * @return int < 0 为超出屏幕左侧, int > 0 为超出右侧
     */
    private int getYtoEdge(int y) {
        if (y <= 0) return y;
        int overY = y - getToolkit().getScreenSize().height+getHeight();
        return Math.max(overY, 0);
    }

    /**
     * 检测屏幕边缘碰撞
     * @param dx x方向移动量
     * @param dy y方向移动量
     * @return 返回一个可能包含 null 的 <b>_2Elems(x超边界距离 或 null, y超边界距离 或 null)</b>
     */
    private _2Elems<Integer, Integer> getEdgeHit(double dx, double dy) {
        Dimension screen = getToolkit().getScreenSize();
        _2Elems<Integer, Integer> distance = new _2Elems<>(null, null);

        if (getX() <= 0 && dx < 0) {
            distance.e1 = getX();
        } else if (getX() >= screen.width-getWidth() && dx > 0) {
            distance.e1 = getX() + getWidth() - screen.width;
        }

        if (getY() <= 0 && dy < 0) {
            distance.e2 = getY();
        } else if (getY() >= screen.height-getHeight() && dy > 0) {
            distance.e2 = getY() + getHeight() - screen.height;
        }
        return distance;
    }


}
