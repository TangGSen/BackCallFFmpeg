package com.ffmpeger.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

/**
 * Created by neokree on 24/11/14.
 */
public class GeneralCommandFragment extends Fragment {
    private static final String TAG = GeneralCommandFragment.class.getSimpleName();

    private EditText commandEditText;
    private LinearLayout outputLayout;
    private Button runButton;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_general_command, null);

        commandEditText = (EditText)rootView.findViewById(R.id.command_text);
        outputLayout = (LinearLayout)rootView.findViewById(R.id.command_output);
        runButton = (Button)rootView.findViewById(R.id.run_command);

        if (null != runButton) {
            runButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String command = commandEditText.getText().toString();
                    if (!TextUtils.isEmpty(command)) {
                        execFFmpegBinary(command);
                    } else {
                        commandEditText.setText(R.string.ffmpeg_cmd_show_version);
                        Toast.makeText(getActivity(), getString(R.string.empty_command_toast),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(null);

        try {
            FFmpegExecutor.getInstance(activity).loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }

    private void execFFmpegBinary(final String command) {
        try {
            FFmpegExecutor.getInstance(getActivity()).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    addTextViewToLayout("FAILED with output : "+s);
                }

                @Override
                public void onSuccess(String s) {
                    addTextViewToLayout("SUCCESS with output : "+s);
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    addTextViewToLayout("progress : "+s);
                    progressDialog.setMessage("Processing\n"+s);
                }

                @Override
                public void onStart() {
                    outputLayout.removeAllViews();

                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg "+command);
                    progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    private void addTextViewToLayout(String text) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        outputLayout.addView(textView);
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .create()
                .show();

    }

}
