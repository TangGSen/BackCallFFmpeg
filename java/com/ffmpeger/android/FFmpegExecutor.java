package com.ffmpeger.android;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

/**
 * Created by yangfeng on 15/1/16.
 */
public class FFmpegExecutor {
    private static FFmpegExecutor _executor;
    public static FFmpegExecutor getInstance(Context context) {
        if (null == _executor) {
            _executor = new FFmpegExecutor(context);
        }

        return _executor;
    }

    private FFmpeg ffmpeg;

    private FFmpegExecutor(Context context) {
        ffmpeg = FFmpeg.getInstance(context.getApplicationContext());
    }

    public void loadBinary(LoadBinaryResponseHandler handler) throws FFmpegNotSupportedException {
        ffmpeg.loadBinary(handler);
    }

    public void execute(String command, ExecuteBinaryResponseHandler handler) throws FFmpegCommandAlreadyRunningException {
        ffmpeg.execute(command, handler);
    }
}
