package com.ilibellus.helpers;

import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

import com.ilibellus.Ilibellus;
import com.ilibellus.models.Attachment;

public class FileProviderHelper {

    /**
     * Generates the FileProvider URI for a given existing file
     */
    public static Uri getFileProvider(File file) {
        return FileProvider.getUriForFile(Ilibellus.getAppContext(), Ilibellus.getAppContext().getPackageName() + ".authority", file);
    }

    /**
     * Generates a shareable URI for a given attachment by evaluating its stored (into DB) path
     */
    public static Uri getShareableUri(Attachment attachment) {
        File attachmentFile = new File(attachment.getUri().getPath());
        if (attachmentFile.exists()) {
            return FileProviderHelper.getFileProvider(attachmentFile);
        } else {
            return attachment.getUri();
        }
    }
}
