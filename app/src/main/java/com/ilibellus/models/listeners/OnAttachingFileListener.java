package com.ilibellus.models.listeners;

import com.ilibellus.models.Attachment;


public interface OnAttachingFileListener {

    public void onAttachingFileErrorOccurred(Attachment mAttachment);

    public void onAttachingFileFinished(Attachment mAttachment);
}
