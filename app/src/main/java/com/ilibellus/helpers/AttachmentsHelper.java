package com.ilibellus.helpers;


import org.apache.commons.io.FileUtils;

import java.io.File;

import com.ilibellus.models.Attachment;


public class AttachmentsHelper {

	/**
	 * Retrieves attachment file size
	 *
	 * @param attachment Attachment to evaluate
	 * @return Human readable file size string
	 */
	public static String getSize(Attachment attachment) {
		long sizeInKb = attachment.getSize();
		if (attachment.getSize() == 0) {
			sizeInKb = new File(attachment.getUri().getPath()).length();
		}
		return FileUtils.byteCountToDisplaySize(sizeInKb);
	}

	/**
	 * Checks type of attachment
	 *
	 * @param attachment
	 * @return
	 */
	public static boolean typeOf(Attachment attachment, String... mimeTypes) {
		for (String mimeType : mimeTypes) {
			if (mimeType.equals(attachment.getMime_type())) return true;
		}
		return false;
	}
}
