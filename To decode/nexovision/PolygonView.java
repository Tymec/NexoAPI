package eu.nexwell.android.nexovision;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v4.content.res.ResourcesCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Polygon;
import java.util.ArrayList;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class PolygonView extends View {
    private static int defaultColor;
    private static int editingColor;
    private static Runnable longPressTimer = null;
    private static int touchedColor;
    private Polygon _polygon;
    private Point currPoint = null;
    private Paint paint;
    private Paint paint_bg;
    private Point touchPoint = null;

    /* renamed from: eu.nexwell.android.nexovision.PolygonView$1 */
    class C20531 implements OnTouchListener {
        C20531() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (!PolygonView.this._polygon.isInEditmode()) {
                if (motionEvent.getAction() == 0) {
                    if (PolygonView.this.contains(new Point((int) motionEvent.getX(), (int) motionEvent.getY()))) {
                        PolygonView.this.paint.setColor(PolygonView.touchedColor);
                        PolygonView.this.paint.setAlpha(0);
                        PolygonView.this.paint_bg.setColor(PolygonView.touchedColor);
                        PolygonView.this.paint_bg.setAlpha((PolygonView.this._polygon.getAlpha() * 255) / 100);
                        PolygonView.this.bringToFront();
                        final IElement el = NVModel.getElementById(PolygonView.this._polygon.getId());
                        ArrayList<ISet> places = NVModel.getSetsByElementId(PolygonView.this._polygon.getId().intValue());
                        NVModel.removeElement(el, true);
                        NVModel.addElement(el);
                        Iterator<ISet> itrs = places.iterator();
                        while (itrs.hasNext()) {
                            ((ISet) itrs.next()).addElement(el);
                        }
                        PolygonView.this.invalidate();
                        MainActivity.handler.postDelayed(PolygonView.longPressTimer = new Runnable() {
                            public void run() {
                                NVModel.CURR_ELEMENT = el;
                                RoomFragment.longPressMenuDialog.show();
                            }
                        }, 500);
                        return true;
                    }
                } else if (motionEvent.getAction() == 1) {
                    if (PolygonView.longPressTimer != null) {
                        MainActivity.handler.removeCallbacks(PolygonView.longPressTimer);
                    }
                    PolygonView.this.paint.setColor(PolygonView.defaultColor);
                    PolygonView.this.paint.setAlpha(0);
                    PolygonView.this.paint_bg.setColor(PolygonView.defaultColor);
                    PolygonView.this.paint_bg.setAlpha((PolygonView.this._polygon.getAlpha() * 255) / 100);
                    PolygonView.this.invalidate();
                    return true;
                }
                return false;
            } else if (motionEvent.getAction() == 0) {
                Point p;
                PolygonView.this.paint.setColor(PolygonView.editingColor);
                PolygonView.this.paint.setAlpha(255);
                PolygonView.this.paint_bg.setColor(PolygonView.editingColor);
                PolygonView.this.paint_bg.setAlpha((PolygonView.this._polygon.getAlpha() * 255) / 100);
                PolygonView.this.currPoint = null;
                PolygonView.this.touchPoint = new Point((int) motionEvent.getX(), (int) motionEvent.getY());
                Iterator<Point> itrp = PolygonView.this._polygon.getPointsAsArray().iterator();
                while (itrp.hasNext()) {
                    p = (Point) itrp.next();
                    if (PolygonView.pointToPointDistance(PolygonView.this.touchPoint, p) < ((double) (((float) Math.min(PolygonView.this.getMeasuredHeight(), PolygonView.this.getMeasuredWidth())) / 20.0f))) {
                        PolygonView.this.currPoint = p;
                    }
                }
                if (PolygonView.this.currPoint == null) {
                    if (PolygonView.this._polygon.getPointsAsArray().size() > 1) {
                        Point prevPoint = (Point) PolygonView.this._polygon.getPointsAsArray().get(PolygonView.this._polygon.getPointsAsArray().size() - 1);
                        itrp = PolygonView.this._polygon.getPointsAsArray().iterator();
                        while (itrp.hasNext()) {
                            p = (Point) itrp.next();
                            if (p != prevPoint && PolygonView.pointToLineDistance(p, prevPoint, PolygonView.this.touchPoint) < ((double) (((float) Math.min(PolygonView.this.getMeasuredHeight(), PolygonView.this.getMeasuredWidth())) / 40.0f))) {
                                PolygonView.this.currPoint = PolygonView.this._polygon.addPointBefore(PolygonView.this.touchPoint.x, PolygonView.this.touchPoint.y, p);
                                break;
                            }
                            prevPoint = p;
                        }
                    } else {
                        PolygonView.this.currPoint = PolygonView.this._polygon.addPoint(PolygonView.this.touchPoint.x, PolygonView.this.touchPoint.y);
                    }
                }
                PolygonView.this.invalidate();
                return true;
            } else if (motionEvent.getAction() != 2 || PolygonView.this.currPoint == null) {
                return true;
            } else {
                PolygonView.this.currPoint.x = (int) motionEvent.getX();
                PolygonView.this.currPoint.y = (int) motionEvent.getY();
                PolygonView.this.invalidate();
                return true;
            }
        }
    }

    public PolygonView(Context context, Polygon polygon) {
        super(context);
        this._polygon = polygon;
        defaultColor = ResourcesCompat.getColor(getResources(), R.color.green_light, null);
        touchedColor = ResourcesCompat.getColor(getResources(), R.color.green, null);
        editingColor = ResourcesCompat.getColor(getResources(), R.color.blue_light, null);
        this.paint = new Paint();
        this.paint.setStyle(Style.FILL);
        this.paint.setColor(defaultColor);
        this.paint.setAlpha(0);
        this.paint_bg = new Paint();
        this.paint_bg.setStyle(Style.FILL);
        this.paint_bg.setColor(defaultColor);
        this.paint_bg.setAlpha((this._polygon.getAlpha() * 255) / 100);
        setOnTouchListener(new C20531());
    }

    public boolean contains(Point test) {
        boolean result = false;
        Point[] points = (Point[]) this._polygon.getPointsAsArray().toArray(new Point[this._polygon.getPointsAsArray().size()]);
        int i = 0;
        int j = points.length - 1;
        while (i < points.length) {
            Object obj;
            if (points[i].y > test.y) {
                obj = 1;
            } else {
                obj = null;
            }
            if (obj != (points[j].y > test.y ? 1 : null) && test.x < (((points[j].x - points[i].x) * (test.y - points[i].y)) / (points[j].y - points[i].y)) + points[i].x) {
                if (result) {
                    result = false;
                } else {
                    result = true;
                }
            }
            j = i;
            i++;
        }
        return result;
    }

    public void setPolygon(Polygon polygon) {
        this._polygon = polygon;
    }

    public Polygon getPolygon() {
        return this._polygon;
    }

    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        ArrayList<Point> points = this._polygon.getPointsAsArray();
        Iterator<Point> itrp = points.iterator();
        boolean first = true;
        while (itrp.hasNext()) {
            Point p = (Point) itrp.next();
            if (first) {
                first = false;
                path.moveTo((float) p.x, (float) p.y);
            } else {
                path.lineTo((float) p.x, (float) p.y);
            }
        }
        path.close();
        canvas.drawPath(path, this.paint_bg);
        this.paint.setStrokeWidth(((float) Math.min(canvas.getHeight(), canvas.getWidth())) / 100.0f);
        this.paint.setStrokeCap(Cap.ROUND);
        itrp = points.iterator();
        Point p_prev = null;
        while (itrp.hasNext()) {
            p = (Point) itrp.next();
            if (p_prev != null) {
                canvas.drawLine((float) p_prev.x, (float) p_prev.y, (float) p.x, (float) p.y, this.paint);
            }
            if (this._polygon.isInEditmode()) {
                canvas.drawCircle((float) p.x, (float) p.y, ((float) Math.min(canvas.getHeight(), canvas.getWidth())) / 100.0f, this.paint);
            }
            p_prev = p;
        }
        if (p_prev != null && points != null && points.size() > 1) {
            canvas.drawLine((float) p_prev.x, (float) p_prev.y, (float) ((Point) points.get(0)).x, (float) ((Point) points.get(0)).y, this.paint);
        }
    }

    public static double pointToPointDistance(Point a, Point b) {
        return Math.hypot((double) (b.x - a.x), (double) (b.y - a.y));
    }

    public static double pointToLineDistance(Point A, Point B, Point P) {
        return ((double) Math.abs(((P.x - A.x) * (B.y - A.y)) - ((P.y - A.y) * (B.x - A.x)))) / Math.sqrt((double) (((B.x - A.x) * (B.x - A.x)) + ((B.y - A.y) * (B.y - A.y))));
    }
}
