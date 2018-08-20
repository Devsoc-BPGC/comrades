package com.macbitsgoa.comrades.coursematerial;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;
import static com.macbitsgoa.comrades.GetGoogleSignInActivity.KEY_TOKEN;

public class UploadFileFragment extends DialogFragment
        implements View.OnClickListener {
    private static final String TAG = TAG_PREFIX + UploadFileFragment.class.getSimpleName();
    private static final int SIGN_IN_REQUEST_CODE = 10001;
    private static final int REQUEST_CHOOSER = 1;
    private Dialog.OnClickListener positiveClickListener;
    private EditText fileName;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabAddDoc;
    private FloatingActionButton fabAddImage;
    private TextView file;
    private String filePath;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_upload_file, null);

        initUi(view);

        setPositiveClick();
        fabAdd.setOnClickListener(this);
        fabAddDoc.setOnClickListener(this);
        fabAddImage.setOnClickListener(this);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(getString(R.string.add_file), positiveClickListener)
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                })
                .create();


    }

    /**
     * initiating Ui.
     *
     * @param view rootView to display
     */
    private void initUi(final View view) {
        fabAdd = view.findViewById(R.id.fab_all_files);
        fabAddDoc = view.findViewById(R.id.fab_doc);
        fabAddImage = view.findViewById(R.id.fab_image);
        fileName = view.findViewById(R.id.et_file_name);
        file = view.findViewById(R.id.tv_file_path);

    }

    private void setPositiveClick() {
        positiveClickListener = (dialogInterface, i) -> {
            if (file.getText().length() == 0) {
                Toast.makeText(getContext(), R.string.warn_select_file, Toast.LENGTH_LONG).show();
                return;
            }

            if (fileName.getText().length() == 0) {
                Toast.makeText(getContext(), R.string.warn_empty_file_name, Toast.LENGTH_LONG).show();
                return;
            }
            final Fragment dialogFragment = getActivity().getSupportFragmentManager()
                    .findFragmentByTag(CourseActivity.ADD_FILE_FRAGMENT);
            final Intent signInIntent = new Intent(getActivity(), GetGoogleSignInActivity.class);
            dialogFragment.startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
        };


    }

    /**
     * Listener for select File fab.
     *
     * @param view clicked view
     */
    @Override
    public void onClick(final View view) {
        final UploadFileFragment dialogFragment = (UploadFileFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(CourseActivity.ADD_FILE_FRAGMENT);
        switch (view.getId()) {
            case R.id.fab_image:
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                dialogFragment.startActivityForResult(Intent.createChooser(imageIntent, "Select Picture"), REQUEST_CHOOSER);
                break;

            case R.id.fab_doc:
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                fileIntent.setType("application/pdf");
                dialogFragment.startActivityForResult(Intent.createChooser(fileIntent, "Select Pdf"), REQUEST_CHOOSER);
                break;

            case R.id.fab_all_files:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                dialogFragment.startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CHOOSER);
                break;
            default:
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Invalid onClickListener");
                }
        }
    }

    /**
     * function to handle callbacks
     *
     * @param requestCode code with which request was sent
     * @param resultCode  code to check if everything went well
     * @param data        data received with callback
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE && resultCode == RESULT_OK) {
            final String accessToken = data.getStringExtra(KEY_TOKEN);
            Intent uploadIntent = UploadService.makeUploadIntent(getContext(), filePath,
                    accessToken, fileName.getText().toString());
            Toast.makeText(getContext(), "Upload Started.Check NotificationBar for progress.",
                    Toast.LENGTH_LONG).show();
            getActivity().startService(uploadIntent);
        } else if (resultCode == RESULT_OK) {
            filePath = PathUtil.getPath(getContext(), data.getData());
            if (filePath != null) {
                File tempFile = new File(filePath);
                file.setText(tempFile.getName());
            } else {
                Log.e(TAG, data.getData().toString());
                Toast.makeText(getContext(), "Problem Selecting File.Please check if that file actually exists on your device.", Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "No File Selected", Toast.LENGTH_LONG).show();
        }

    }

}
