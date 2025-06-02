package KAGO_framework.view;

/**
 * Stellt eine Zeichenfläche in einem DrawFrame-Fenster dar. Beim DrawingPanel über die Methode "add" registrierte
 * Objekte werden vom Panel gezeichnet. Außerdem kümmert sich das DrawingPanel um das Aufrufen der im framework
 * realisierten Callbacks wie etwa update und draw.
 * Diese Modellierung ist nicht sauber, da das DrawingPanel damit Funktionen eines Controllers übernimmt.
 * Vorgegebene Klasse des Frameworks. Modifikation auf eigene Gefahr.
 */
public class DrawingPanel  {

    /*private boolean requested = false;

    private DrawTool drawTool;
    private ViewController viewController;

    public DrawingPanel(ViewController viewController){
        super();
        this.viewController = viewController;
        setDoubleBuffered(true);
        this.drawTool = new DrawTool();
    }

    @Override
    public void paintComponent(Graphics g) {
        if(!requested){
            addMouseListener(viewController);
            addKeyListener(viewController);
            addMouseMotionListener(viewController);
            setFocusable(true);
            requestFocusInWindow();
            requested = ! requested;
        }
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawTool.setGraphics2D(g2d,this);
        TimerUtils.update();

        this.cameraController.attach(drawTool);
        this.viewController.drawAndUpdateObjects(drawTool, TimerUtils.getDeltaTime());
        this.cameraController.detach(drawTool);
    }

    public CameraController getCameraController() {
        return this.cameraController;
    }*/
}

