package com.kangjj.ndk.bsdiff;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * 这节课代码不多 主要看视频或者xmind大纲
 */
public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.tv_version);
        tv.setText("当前版本：" + BuildConfig.VERSION_NAME);
    }

    public void onUpdate(View view) {
        new AsyncTask<Void,Void, File>(){
            @Override
            protected File doInBackground(Void... voids) {
                //bspatch做合成 得到新版本的apk文件。
//                String patch= new File(Environment.getExternalStorageDirectory(),"patch.diff").getAbsolutePath();
                String patch= new File("/sdcard/","patch.diff").getAbsolutePath();//TODO 服务器生成的差量包，通过下载获取
                File newApk =new File("/sdcard/"/*Environment.getExternalStorageDirectory()*/,"new.apk");//旧包通过差量包生成的新包
                if(!newApk.exists()){
                    try {
                        newApk.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String oldApk = getApplicationInfo().sourceDir;//TODO 旧包保存的位置通过此方法获取
                doPatchNative(oldApk,newApk.getAbsolutePath(),patch);
                return newApk;
            }

            @Override
            protected void onPostExecute(File apkFile) {
                if(!apkFile.exists())
                    return;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(Build.VERSION.SDK_INT>=24){
                    // 参数2 清单文件中provider节点里面的authorities ; 参数3  共享的文件,即apk包的file类
                    Uri apkUri = FileProvider.getUriForFile(MainActivity.this,
                            getApplicationInfo().packageName + ".provider", apkFile);
                    //对目标应用临时授权该Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                }else{
                    intent.setDataAndType(Uri.fromFile(apkFile),
                            "application/vnd.android.package-archive");
                }
                startActivity(intent);
            }
        }.execute();
    }

    private native void doPatchNative(String oldApk,String newApk,String patch);
}
