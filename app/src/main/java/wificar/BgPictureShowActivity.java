package wificar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import my.wificar.R;

/*小R科技（www.xiao-r.com）旗下
 * 中国FPV-WIFI机器人网·机器人创意工作室（www.wifi-robots.com）对此源码拥有所有权
 * 此源码仅供用户学习之用，严禁用作商业牟利
 * 如发现侵权行为，小R科技将择机通过法律途径予以起诉！
 * */
public class BgPictureShowActivity extends Activity implements AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
    /*
     *
     * 这个类用于显示保存下来的图片，通过gallery控件，将SD卡指定路径中的图片
     * 一一读出，并显示在屏幕上。
     *
     * */
    private List<String> imagesList;
    private String[] imagesArray;
    private ImageSwitcher mSwitcher;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictureshow);

        imagesList = getPathOfImgs();
        imagesArray = imagesList.toArray(new String[0]);

        mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
        mSwitcher.setFactory(this);

        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        mSwitcher.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //Toast.makeText(BgPictureShowActivity.this, "点击了", Toast.LENGTH_SHORT).show();

            }

        });

        Gallery g = (Gallery) findViewById(R.id.mygallery);
        g.setAdapter(new ImageAdapter(this, imagesList));
        g.setOnItemSelectedListener(this);
        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                Toast.makeText(BgPictureShowActivity.this, "第" + (position + 1) + "张图片", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //获取图片路径
    private List<String> getPathOfImgs() {

        List<String> it = new ArrayList<String>();
        File f = new File("/sdcard/demo/");
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (getImageFile(file.getPath()))
                it.add(file.getPath());
        }
        return it;
    }

    private boolean getImageFile(String fName) {
        boolean re;
        String end = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            re = true;
        } else {
            re = false;
        }
        return re;
    }


    public class ImageAdapter extends BaseAdapter {

        int mGalleryItemBackground;
        private Context mContext;
        private List<String> lis;


        public ImageAdapter(Context c, List<String> li) {
            mContext = c;
            lis = li;

            TypedArray a = obtainStyledAttributes(R.styleable.Gallery);

            mGalleryItemBackground = a.getResourceId(
                    R.styleable.Gallery_android_galleryItemBackground, 0);

            a.recycle();
        }


        public int getCount() {
            return lis.size();
        }

        public Object getItem(int position) {
            return position;
        }


        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView,
                            ViewGroup parent) {

            ImageView i = new ImageView(mContext);

            Bitmap bm = BitmapFactory.decodeFile(lis.get(position));
            i.setImageBitmap(bm);

            i.setScaleType(ImageView.ScaleType.FIT_XY);

            i.setLayoutParams(new Gallery.LayoutParams(480, 320));

            i.setBackgroundResource(mGalleryItemBackground);

            return i;
        }
    }


    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        String photoURL = imagesArray[position];
        Log.i("A", String.valueOf(position));

        mSwitcher.setImageURI(Uri.parse(photoURL));

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    public View makeView() {
        ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        return i;
    }
}
