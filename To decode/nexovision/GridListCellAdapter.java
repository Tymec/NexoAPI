package eu.nexwell.android.nexovision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.nexwell.android.nexovision.model.AnalogOutput;
import eu.nexwell.android.nexovision.model.AnalogSensor;
import eu.nexwell.android.nexovision.model.Dimmer;
import eu.nexwell.android.nexovision.model.GeolocationPoint;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.ISwitch;
import eu.nexwell.android.nexovision.model.Logic;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Partition;
import eu.nexwell.android.nexovision.model.Partition.Function;
import eu.nexwell.android.nexovision.model.Set;
import eu.nexwell.android.nexovision.model.Thermometer;
import eu.nexwell.android.nexovision.model.Ventilator;
import java.util.List;
import nexovision.android.nexwell.eu.nexovision.R;
import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

public class GridListCellAdapter extends BaseDynamicGridAdapter {
    private Bitmap[] _bitmaps;
    private Context _context;
    private LayoutInflater _inflater;
    private int[] _layout;
    private Object[] _objects;
    private int lastPosition = -1;
    private View zeroPositionView = null;

    static class ViewHolder {
        LinearLayout box;
        ImageView icon;
        ImageView icon2;
        ImageView image;
        IconLabel label;
        LinearLayout subbox2;
        TextView text1;
        TextView text2;
        TextView text3;
        TextView text4;

        ViewHolder() {
        }
    }

    public GridListCellAdapter(Context context, int[] textViewResourceId, List<?> objects) {
        super(context, objects, 3);
        this._context = context;
        this._layout = textViewResourceId;
        this._bitmaps = new Bitmap[objects.size()];
        this._inflater = LayoutInflater.from(this._context.getApplicationContext());
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (getItem(position) == null || !((IElement) getItem(position)).getType().equals(NVModel.EL_TYPE_VENTILATOR) || !(((IElement) getItem(position)) instanceof Ventilator) || this._layout.length <= 1) {
                convertView = this._inflater.inflate(this._layout[0], parent, false);
            } else {
                convertView = this._inflater.inflate(this._layout[1], parent, false);
            }
            holder.box = (LinearLayout) convertView.findViewById(R.id.box);
            holder.subbox2 = (LinearLayout) convertView.findViewById(R.id.subbox2);
            holder.label = (IconLabel) convertView.findViewById(R.id.label);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.icon2 = (ImageView) convertView.findViewById(R.id.icon2);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.text1 = (TextView) convertView.findViewById(R.id.text1);
            holder.text2 = (TextView) convertView.findViewById(R.id.text2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.label.setSelected(true);
        holder.label.setEnabled(true);
        if (getItem(position) != null) {
            holder.label.setText(((IElement) getItem(position)).getName());
        }
        if (getItem(position) != null) {
            Drawable d = ((IElement) getItem(position)).getBackground();
            if (d != null) {
                holder.box.setBackground(d);
                if (((IElement) getItem(position)).isSelected()) {
                    d.setState(new int[]{16842913});
                } else {
                    d.setState(new int[0]);
                }
                d.invalidateSelf();
            }
            int resId = this._context.getResources().getIdentifier("drawable/" + ((IElement) getItem(position)).getIcon(), null, this._context.getPackageName());
            if (resId <= 0) {
                resId = this._context.getResources().getIdentifier("drawable/i_1_3d_unknown", null, this._context.getPackageName());
            }
            holder.icon.setImageResource(resId);
            if (((IElement) getItem(position)).getType().equals(NVModel.EL_TYPE_THERMOMETER) && (((IElement) getItem(position)) instanceof Thermometer)) {
                holder.text1.setText(((Thermometer) getItem(position)).getValue() + "℃");
                holder.text1.setVisibility(0);
                if (((Thermometer) getItem(position)).getThermostat() != null) {
                    if (holder.text2 != null) {
                        holder.text2.setText(((Thermometer) getItem(position)).getThermostat().getValue() + "℃");
                        holder.text2.setVisibility(0);
                    } else {
                        holder.text1.setText(holder.text1.getText().toString() + " / " + ((Thermometer) getItem(position)).getThermostat().getValue() + "℃");
                    }
                } else if (holder.text2 == null || holder.icon2 == null) {
                    holder.text1.setVisibility(4);
                } else {
                    holder.subbox2.setVisibility(8);
                }
            } else if (((IElement) getItem(position)).getType().equals(NVModel.EL_TYPE_LOGIC) && (((IElement) getItem(position)) instanceof Logic)) {
                if (holder.text1 != null) {
                    if (((Logic) getItem(position)).getStateLabel() != null) {
                        holder.text1.setText(((Logic) getItem(position)).getStateLabel());
                        holder.text1.setVisibility(0);
                    } else {
                        holder.text1.setVisibility(8);
                    }
                }
            } else if (((IElement) getItem(position)).getType().equals(NVModel.EL_TYPE_GEOLOCATIONPOINT) && (((IElement) getItem(position)) instanceof GeolocationPoint)) {
                if (holder.text1 != null) {
                    if (((GeolocationPoint) getItem(position)).getInfo() != null) {
                        holder.text1.setText(((GeolocationPoint) getItem(position)).getInfo());
                        holder.text1.setVisibility(0);
                    } else {
                        holder.text1.setVisibility(4);
                    }
                }
            } else if (((IElement) getItem(position)).getType().equals(NVModel.EL_TYPE_VENTILATOR) && (((IElement) getItem(position)) instanceof Ventilator)) {
                if (holder.text1 != null) {
                    if (holder.image != null) {
                        holder.image.setVisibility(8);
                    }
                    if (((Ventilator) getItem(position)).getInfo() == null || ((Ventilator) getItem(position)).getInfo().isEmpty()) {
                        holder.text1.setVisibility(4);
                    } else {
                        holder.text1.setText(((Ventilator) getItem(position)).getInfo());
                        holder.text1.setVisibility(0);
                    }
                }
            } else if (((IElement) getItem(position)).getType().equals(NVModel.EL_TYPE_PARTITION) && (((IElement) getItem(position)) instanceof Partition)) {
                if (((Partition) getItem(position)).getFunc() == Function.COMMON) {
                    if (holder.text1 != null) {
                        holder.text1.setVisibility(4);
                    }
                    if (holder.image != null) {
                        holder.image.setVisibility(0);
                    }
                } else {
                    if (holder.text1 != null) {
                        holder.text1.setText(this._context.getResources().getString(R.string.Resource_Partition24h_SymbolLabel));
                        holder.text1.setVisibility(0);
                    }
                    if (holder.image != null) {
                        holder.image.setVisibility(8);
                    }
                }
            } else if (((IElement) getItem(position)).getType().equals(NVModel.EL_TYPE_DIMMER) && (((IElement) getItem(position)) instanceof Dimmer)) {
                if (holder.text1 != null) {
                    holder.text1.setText(((Dimmer) getItem(position)).getValue().intValue() + "%");
                    holder.text1.setVisibility(0);
                }
            } else if (((IElement) getItem(position)).getType().equals(NVModel.EL_TYPE_ANALOGSENSOR) && (((IElement) getItem(position)) instanceof AnalogSensor)) {
                if (holder.text1 != null) {
                    holder.text1.setText(((AnalogSensor) getItem(position)).getValue().intValue() + "");
                    holder.text1.setVisibility(0);
                }
            } else if (((IElement) getItem(position)).getType().equals(NVModel.EL_TYPE_ANALOGOUTPUT) && (((IElement) getItem(position)) instanceof AnalogOutput)) {
                if (holder.text1 != null) {
                    holder.text1.setText(((AnalogOutput) getItem(position)).getValue().intValue() + "%");
                    holder.text1.setVisibility(0);
                }
            } else if (((IElement) getItem(position)).getType().equals(NVModel.EL_TYPE_SET) && (((IElement) getItem(position)) instanceof Set)) {
                if (!(holder.image == null || ((ISet) getItem(position)).getBitmap() == null)) {
                    holder.image.setImageBitmap(((ISet) getItem(position)).getBitmap());
                    holder.image.setVisibility(0);
                }
                if (holder.text1 != null) {
                    Thermometer t = ((ISet) getItem(position)).getThermometer();
                    if (t != null) {
                        holder.text1.setText(t.getValue().intValue() + "," + ((int) ((t.getValue().floatValue() - ((float) t.getValue().intValue())) * 10.0f)) + "℃");
                        holder.text1.setVisibility(0);
                        ((SquareImageView) convertView.findViewById(R.id.icon1)).setVisibility(0);
                        ((LinearLayout) convertView.findViewById(R.id.value1)).setVisibility(0);
                    } else {
                        holder.text1.setVisibility(8);
                        ((SquareImageView) convertView.findViewById(R.id.icon1)).setVisibility(8);
                        ((LinearLayout) convertView.findViewById(R.id.value1)).setVisibility(8);
                    }
                }
                if (holder.text2 != null) {
                    int lights_count = ((ISet) getItem(position)).getLights().size();
                    if (lights_count > 0) {
                        holder.text2.setText(((ISet) getItem(position)).getLightsOn().size() + "/" + lights_count);
                        holder.text2.setVisibility(0);
                        ((SquareImageView) convertView.findViewById(R.id.icon2)).setVisibility(0);
                        ((LinearLayout) convertView.findViewById(R.id.value2)).setVisibility(0);
                    } else {
                        holder.text2.setVisibility(8);
                        ((SquareImageView) convertView.findViewById(R.id.icon2)).setVisibility(8);
                        ((LinearLayout) convertView.findViewById(R.id.value2)).setVisibility(8);
                    }
                }
                if (holder.text1.getVisibility() == 8 && holder.text2.getVisibility() == 8) {
                    ((LinearLayout) convertView.findViewById(R.id.values)).setVisibility(8);
                } else {
                    ((LinearLayout) convertView.findViewById(R.id.values)).setVisibility(0);
                }
            } else {
                if (holder.text1 != null) {
                    holder.text1.setVisibility(4);
                }
                if (holder.text2 != null) {
                    holder.text2.setVisibility(4);
                }
            }
        }
        return convertView;
    }

    public void drawPoolIndicator(Canvas canv, Object object) {
        if (object instanceof ISwitch) {
            Paint paint_circle = new Paint();
            paint_circle.setColor(-12827006);
            paint_circle.setAntiAlias(true);
            Paint paint_text = new Paint();
            paint_text.setTypeface(Typeface.DEFAULT_BOLD);
            paint_text.setColor(-1);
            paint_text.setTextSize(((float) canv.getWidth()) * 0.26f);
            paint_text.setAntiAlias(true);
            Rect bounds = new Rect();
            if (!((ISwitch) object).isUpdated()) {
                paint_text.getTextBounds("?", 0, 1, bounds);
                canv.drawCircle(((float) canv.getWidth()) * 0.2f, ((float) canv.getWidth()) * 0.2f, ((float) canv.getWidth()) * 0.15f, paint_circle);
                canv.drawText("?", ((float) canv.getWidth()) * 0.135f, ((float) canv.getWidth()) * 0.29f, paint_text);
            }
        }
    }

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        int width = (int) (((float) bitmap.getHeight()) * 1.5f);
        Bitmap cropImg = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - width) / 2, 0, width, bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(cropImg.getWidth(), cropImg.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        BitmapShader shader = new BitmapShader(cropImg, TileMode.REPEAT, TileMode.REPEAT);
        Paint paint = new Paint();
        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
        paint.setShader(shader);
        RoundRectShape mRoundShape = new RoundRectShape(new float[]{80.0f, 80.0f, 80.0f, 80.0f, 0.0f, 0.0f, 0.0f, 0.0f}, null, null);
        mRoundShape.resize((float) cropImg.getWidth(), (float) cropImg.getHeight());
        mRoundShape.draw(canvas, paint);
        return output;
    }
}
