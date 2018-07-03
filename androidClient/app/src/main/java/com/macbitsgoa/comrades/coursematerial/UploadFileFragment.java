package com.macbitsgoa.comrades.coursematerial;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;
import static com.macbitsgoa.comrades.GetGoogleSignInActivity.KEY_TOKEN;

public class UploadFileFragment extends DialogFragment
        implements View.OnClickListener, StorageChooser.OnSelectListener {
    private static final String TAG = TAG_PREFIX + CourseActivity.class.getSimpleName();
    private static final int SIGN_IN_REQUEST_CODE = 10001;
    private Dialog.OnClickListener positiveClickListener;
    private EditText fileName;
    private FloatingActionButton fabAddFile;
    private TextView filePath;
    private StorageChooser chooser;


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_upload_file, null);

        initUi(view);

        setPositiveClick();
        fabAddFile.setOnClickListener(this);
        chooser.setOnSelectListener(this);

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
        fabAddFile = view.findViewById(R.id.fab_select_file);
        fileName = view.findViewById(R.id.et_file_name);
        filePath = view.findViewById(R.id.tv_file_path);

        chooser = new StorageChooser.Builder()
                .withActivity(getActivity())
                .withFragmentManager(getActivity().getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build();

    }

    private void setPositiveClick() {
        positiveClickListener = (dialogInterface, i) -> {
            if (filePath.getText().length() == 0) {
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
        chooser.show();
    }

    /**
     * Callback after the user chooses file.
     *
     * @param path path of the file selected by the user.
     */
    @Override
    public void onSelect(final String path) {
        filePath.setText(path);
    }

    /**
     * function to handle callback from GetGoogleSignInActivity().
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
            Log.e(TAG, accessToken);
            final UploadFile uploadFile = new UploadFile(filePath.getText().toString(),
                    accessToken, fileName.getText().toString());
            uploadFile.execute();
        }

    }

}
