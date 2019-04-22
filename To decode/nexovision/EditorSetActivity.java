package eu.nexwell.android.nexovision;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import eu.nexwell.android.nexovision.misc.XMLProject;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Thermometer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import nexovision.android.nexwell.eu.nexovision.R;

public class EditorSetActivity extends AppCompatActivity {
    private static IElement TEMP_ELEMENT;
    private static Context context;
    private static FloatingActionButton fab;
    private static Handler handler;
    int REQUEST_CAMERA = 2;
    int SELECT_FILE = 1;
    private Button buttonFromGallery;
    private Button buttonTakePhoto;
    private String filePathWallpaper;
    private LinearLayout imageButtons;
    private ImageView imageWallpaper;
    private EditText inputName;
    private String mCurrentPhotoPath;
    private ScrollView scrollView;
    private CustomSpinner spinnerIconsSize;
    private CustomSpinner spinnerThermometer;

    /* renamed from: eu.nexwell.android.nexovision.EditorSetActivity$1 */
    class C19241 implements OnClickListener {
        C19241() {
        }

        public void onClick(View v) {
            String images_dir = new File(XMLProject.defaultProject).getParent();
            if (images_dir != null) {
                EditorSetActivity.this.mCurrentPhotoPath = images_dir + File.separator + "wallpaper_id" + EditorSetActivity.TEMP_ELEMENT.getId() + ".jpg";
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra("output", FileProvider.getUriForFile(EditorSetActivity.context, EditorSetActivity.this.getApplicationContext().getPackageName() + ".fileprovider", new File(EditorSetActivity.this.mCurrentPhotoPath)));
                EditorSetActivity.this.startActivityForResult(intent, EditorSetActivity.this.REQUEST_CAMERA);
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.EditorSetActivity$2 */
    class C19252 implements OnClickListener {
        C19252() {
        }

        public void onClick(View v) {
            EditorSetActivity.this.startActivityForResult(Intent.createChooser(new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI), "Select File"), EditorSetActivity.this.SELECT_FILE);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.EditorSetActivity$3 */
    class C19263 implements OnClickListener {
        C19263() {
        }

        public void onClick(View view) {
            if (NVModel.CURR_ELEMENT != null) {
                if (EditorSetActivity.this.saveFormToElementModel(NVModel.CURR_ELEMENT)) {
                    Snackbar.make(MainActivity.fragment, EditorSetActivity.getContext().getString(R.string.EditorActivity_SaveOKMessage), 0).setAction("Action", null).show();
                    EditorSetActivity.this.finish();
                    return;
                }
                Snackbar.make(view, EditorSetActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            } else if (EditorSetActivity.this.saveFormToElementModel(EditorSetActivity.TEMP_ELEMENT)) {
                NVModel.addElement(EditorSetActivity.TEMP_ELEMENT);
                NVModel.getCategory(NVModel.CATEGORY_PLACES).addElement(EditorSetActivity.TEMP_ELEMENT);
                Snackbar.make(view, EditorSetActivity.getContext().getString(R.string.EditorActivity_AddOKMessage), 0).setAction("Action", null).show();
                EditorSetActivity.this.finish();
            } else {
                Snackbar.make(view, EditorSetActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            }
        }
    }

    private class ScrollPositionObserver implements OnScrollChangedListener {
        private int mImageViewHeight;

        public ScrollPositionObserver() {
            this.mImageViewHeight = EditorSetActivity.this.getResources().getDimensionPixelSize(R.dimen.activity_editor_image_height);
        }

        public void onScrollChanged() {
            if (EditorSetActivity.TEMP_ELEMENT != null && (EditorSetActivity.TEMP_ELEMENT instanceof ISet)) {
                int scrollY = Math.min(Math.max(EditorSetActivity.this.scrollView.getScrollY(), 0), this.mImageViewHeight);
                EditorSetActivity.this.imageWallpaper.setTranslationY((float) (scrollY / 2));
                EditorSetActivity.this.imageWallpaper.getLayoutParams().height = this.mImageViewHeight - (scrollY / 2);
                EditorSetActivity.this.imageWallpaper.requestLayout();
                EditorSetActivity.this.imageButtons.setTranslationY((float) (scrollY / 2));
                EditorSetActivity.this.imageButtons.getLayoutParams().height = this.mImageViewHeight - (scrollY / 2);
                EditorSetActivity.this.imageButtons.requestLayout();
                float f = ((float) scrollY) / ((float) this.mImageViewHeight);
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_editor_set);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.scrollView = (ScrollView) findViewById(R.id.scroll);
        this.imageButtons = (LinearLayout) findViewById(R.id.image_buttons);
        this.imageWallpaper = (ImageView) findViewById(R.id.image_wallpaper);
        this.buttonTakePhoto = (Button) findViewById(R.id.button_takephoto);
        this.buttonFromGallery = (Button) findViewById(R.id.button_fromgallery);
        this.inputName = (EditText) findViewById(R.id.input_name);
        this.spinnerThermometer = (CustomSpinner) findViewById(R.id.spinner_thermometer);
        this.spinnerIconsSize = (CustomSpinner) findViewById(R.id.spinner_iconssize);
        this.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ScrollPositionObserver());
        this.buttonTakePhoto.setOnClickListener(new C19241());
        this.buttonFromGallery.setOnClickListener(new C19252());
        ArrayList<String> icon_sizes = new ArrayList();
        icon_sizes.add("large");
        icon_sizes.add("big");
        icon_sizes.add(Param.MEDIUM);
        icon_sizes.add("small");
        icon_sizes.add("tiny");
        if (icon_sizes.isEmpty()) {
            this.spinnerIconsSize.setVisibility(8);
        } else {
            this.spinnerIconsSize.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, icon_sizes));
            this.spinnerIconsSize.setSelection(2);
            this.spinnerIconsSize.setVisibility(0);
        }
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C19263());
        }
        if (NVModel.CURR_ELEMENT != null) {
            TEMP_ELEMENT = NVModel.newElement(NVModel.CURR_ELEMENT.getType());
            TEMP_ELEMENT.setId(NVModel.CURR_ELEMENT.getId().intValue());
            TEMP_ELEMENT.setName(NVModel.CURR_ELEMENT.getName());
            TEMP_ELEMENT.setBackgrounds(NVModel.CURR_ELEMENT.getBackgrounds());
            ((ISet) TEMP_ELEMENT).setIconsSize(((ISet) NVModel.CURR_ELEMENT).getIconsSize());
            final Toolbar toolbar2 = toolbar;
            handler.post(new Runnable() {
                public void run() {
                    toolbar2.setTitle(NVModel.getElementTypeName(EditorSetActivity.getContext(), NVModel.CURR_ELEMENT.getType()));
                }
            });
            this.inputName.setText(NVModel.CURR_ELEMENT.getName());
            if (!(((ISet) NVModel.CURR_ELEMENT).getWallpaper() == null || ((ISet) NVModel.CURR_ELEMENT).getWallpaper().isEmpty() || !new File(new File(XMLProject.defaultProject).getParent() + File.separator + ((ISet) NVModel.CURR_ELEMENT).getWallpaper()).exists())) {
                Log.d("EditorActivity", "LOAD file: " + new File(XMLProject.defaultProject).getParent() + File.separator + ((ISet) NVModel.CURR_ELEMENT).getWallpaper());
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screenwidth = size.x;
                int screenheight = size.y;
                Options options = new Options();
                options.inJustDecodeBounds = true;
                options.inPreferredConfig = Config.ARGB_8888;
                BitmapFactory.decodeFile(new File(XMLProject.defaultProject).getParent() + File.separator + ((ISet) NVModel.CURR_ELEMENT).getWallpaper(), options);
                options.inSampleSize = calculateInSampleSize(options, screenwidth, screenheight);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(new File(XMLProject.defaultProject).getParent() + File.separator + ((ISet) NVModel.CURR_ELEMENT).getWallpaper(), options);
                if (bitmap != null) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate((float) getImageAngle(new File(XMLProject.defaultProject).getParent() + File.separator + ((ISet) NVModel.CURR_ELEMENT).getWallpaper()));
                    final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    handler.post(new Runnable() {
                        public void run() {
                            EditorSetActivity.this.imageWallpaper.setImageBitmap(rotatedBitmap);
                        }
                    });
                }
            }
            this.spinnerIconsSize.setSelection(((ISet) NVModel.CURR_ELEMENT).getIconsSize());
            ArrayList<String> thermometers = ((ISet) NVModel.CURR_ELEMENT).getElementNamesByType(NVModel.EL_TYPE_THERMOMETER);
            if (thermometers.isEmpty()) {
                this.spinnerThermometer.setVisibility(8);
            } else {
                if (thermometers.size() < 1) {
                    thermometers.add(getString(R.string.EditorActivity_Form_NoThermometers));
                }
                this.spinnerThermometer.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, thermometers));
                Thermometer t = ((ISet) NVModel.CURR_ELEMENT).getThermometer();
                int index = ((ISet) NVModel.CURR_ELEMENT).getElementsByType(NVModel.EL_TYPE_THERMOMETER).indexOf(t);
                if (t != null && index >= 0) {
                    this.spinnerThermometer.setSelection(index);
                }
                this.spinnerThermometer.setVisibility(0);
            }
            fab.setImageResource(R.drawable.ic_save);
            return;
        }
        TEMP_ELEMENT = NVModel.newElement(NVModel.EL_TYPE_SET);
        this.spinnerThermometer.setVisibility(8);
        fab.setImageResource(R.drawable.ic_add);
    }

    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        if (VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                return Environment.getExternalStorageDirectory() + "/" + DocumentsContract.getDocumentId(uri).split(":")[1];
            } else if (isDownloadsDocument(uri)) {
                uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue());
            } else if (isMediaDocument(uri)) {
                String type = DocumentsContract.getDocumentId(uri).split(":")[0];
                if ("image".equals(type)) {
                    uri = Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if (Param.CONTENT.equalsIgnoreCase(uri.getScheme())) {
            try {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (sourceFile.exists()) {
            FileChannel source = new FileInputStream(sourceFile).getChannel();
            FileChannel destination = new FileOutputStream(destFile).getChannel();
            if (!(destination == null || source == null)) {
                destination.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private boolean saveFormToElementModel(IElement el) {
        IElement element = el;
        if (this.inputName.getText().toString() == null || this.inputName.getText().toString().isEmpty()) {
            return false;
        }
        element.setName(this.inputName.getText().toString());
        if (el != TEMP_ELEMENT) {
            el.setBackgrounds(TEMP_ELEMENT.getBackgrounds());
        }
        Log.d("EditorSetActivity", "saveFormToElementModel(): filePathWallpaper=" + this.filePathWallpaper);
        if (!(this.filePathWallpaper == null || this.filePathWallpaper.isEmpty())) {
            ((ISet) element).setWallpaper(new File(this.filePathWallpaper).getName(), context);
        }
        ((ISet) element).setIconsSize(this.spinnerIconsSize.getSelectedItemPosition());
        if (this.spinnerThermometer.getChildCount() > 1 && NVModel.getElementsByType(NVModel.EL_TYPE_THERMOMETER).size() > 0 && this.spinnerThermometer.getSelectedItemPosition() >= 0) {
            IElement t = (IElement) NVModel.getElementsByType(NVModel.EL_TYPE_THERMOMETER).get(this.spinnerThermometer.getSelectedItemPosition());
            if (t instanceof Thermometer) {
                ((ISet) element).setThermometer((Thermometer) t);
            }
        }
        return true;
    }

    public static Context getContext() {
        return context;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) {
            return;
        }
        if (requestCode == this.SELECT_FILE) {
            onSelectFromGalleryResult(data);
        } else if (requestCode == this.REQUEST_CAMERA) {
            onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        OutputStream outputStream;
        Exception e;
        Throwable th;
        if (data != null) {
            String[] filePathColumn = new String[]{"_data"};
            Cursor cursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);
            cursor.moveToFirst();
            String imgString = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();
            String images_dir = new File(XMLProject.defaultProject).getParent();
            File img = new File(imgString);
            if (images_dir != null && img.exists()) {
                File file = new File(images_dir, "wallpaper_id" + TEMP_ELEMENT.getId() + ".jpg");
                Log.d("EditorActivity", "SAVE img(" + img.getPath() + ") to: " + images_dir + File.separator + "wallpaper_id" + TEMP_ELEMENT.getId() + ".jpg");
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    copyFile(img, file);
                } catch (FileNotFoundException e2) {
                    e2.printStackTrace();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                this.filePathWallpaper = file.getPath();
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screenwidth = size.x;
                int screenheight = size.y;
                Options options = new Options();
                options.inJustDecodeBounds = true;
                options.inPreferredConfig = Config.ARGB_8888;
                BitmapFactory.decodeFile(file.getPath(), options);
                options.inSampleSize = calculateInSampleSize(options, screenwidth, screenheight);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                if (bitmap != null) {
                    final Bitmap bitmap2;
                    Matrix matrix = new Matrix();
                    matrix.postRotate((float) getImageAngle(this.filePathWallpaper));
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    FileOutputStream out = null;
                    try {
                        OutputStream fileOutputStream = new FileOutputStream(file);
                        try {
                            rotatedBitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e32) {
                                    e32.printStackTrace();
                                    outputStream = fileOutputStream;
                                }
                            }
                            outputStream = fileOutputStream;
                        } catch (Exception e4) {
                            e = e4;
                            outputStream = fileOutputStream;
                            try {
                                e.printStackTrace();
                                if (out != null) {
                                    try {
                                        out.close();
                                    } catch (IOException e322) {
                                        e322.printStackTrace();
                                    }
                                }
                                bitmap2 = rotatedBitmap;
                                handler.post(new Runnable() {
                                    public void run() {
                                        EditorSetActivity.this.imageWallpaper.setImageBitmap(bitmap2);
                                    }
                                });
                            } catch (Throwable th2) {
                                th = th2;
                                if (out != null) {
                                    try {
                                        out.close();
                                    } catch (IOException e3222) {
                                        e3222.printStackTrace();
                                    }
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            outputStream = fileOutputStream;
                            if (out != null) {
                                out.close();
                            }
                            throw th;
                        }
                    } catch (Exception e5) {
                        e = e5;
                        e.printStackTrace();
                        if (out != null) {
                            out.close();
                        }
                        bitmap2 = rotatedBitmap;
                        handler.post(/* anonymous class already generated */);
                    }
                    bitmap2 = rotatedBitmap;
                    handler.post(/* anonymous class already generated */);
                }
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        String images_dir = new File(XMLProject.defaultProject).getParent();
        if (images_dir != null) {
            this.filePathWallpaper = images_dir + File.separator + "wallpaper_id" + TEMP_ELEMENT.getId() + ".jpg";
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenwidth = size.x;
            int screenheight = size.y;
            Options options = new Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Config.ARGB_8888;
            BitmapFactory.decodeFile(this.mCurrentPhotoPath, options);
            options.inSampleSize = calculateInSampleSize(options, screenwidth, screenheight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(this.mCurrentPhotoPath, options);
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.postRotate((float) getImageAngle(this.filePathWallpaper));
                final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                handler.post(new Runnable() {
                    public void run() {
                        EditorSetActivity.this.imageWallpaper.setImageBitmap(rotatedBitmap);
                    }
                });
            }
        }
    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = Math.max(options.outHeight, options.outWidth);
        int width = Math.min(options.outHeight, options.outWidth);
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private int getImageAngle(String filePath) {
        int rotationInDegrees = 0;
        try {
            return exifToDegrees(new ExifInterface(filePath).getAttributeInt("Orientation", 1));
        } catch (IOException e) {
            e.printStackTrace();
            return rotationInDegrees;
        } catch (Throwable th) {
            return rotationInDegrees;
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == 6) {
            return 90;
        }
        if (exifOrientation == 3) {
            return 180;
        }
        if (exifOrientation == 8) {
            return 270;
        }
        return 0;
    }
}
