package com.zqf.imdevproject;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class PDFUtil {

    public static void mHandlePdf(ImageView img) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                String path = Environment.getExternalStorageDirectory() + "/a.pdf";
                File file = new File(path);
                ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
                PdfRenderer renderer = new PdfRenderer(descriptor);
                Log.e("TAG", renderer.getPageCount() + "");

                PdfRenderer.Page page = renderer.openPage(1);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                img.setImageBitmap(bitmap);

                page.close();
                renderer.close();
                descriptor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
