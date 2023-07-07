package com.lucy.modmanager.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.preference.Preference;

import com.lucy.modmanager.Constants;
import com.lucy.modmanager.R;
import com.lucy.modmanager.activity.BaseActivity;
import com.lucy.modmanager.activity.JsonEditActivity;

import java.util.Objects;

public class SettingsFragment extends BaseSettingsFragment {
    private static final int TAPS_TO_MANUAL_EDIT = 5;

    private Toast mManualEditHitToast;
    private int mManualEditHitCountdown;

    private final ActivityResultLauncher<Intent> editConfig =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == JsonEditActivity.RESULT_SAVED) {
                    requireActivity().recreate();
                }
            });

    @Override
    public void onStart() {
        super.onStart();
        mManualEditHitCountdown = TAPS_TO_MANUAL_EDIT;
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);

        Preference versionPreference = Objects.requireNonNull(findPreference("version"));
        versionPreference.setOnPreferenceClickListener(p -> {
            if (mManualEditHitCountdown > 0) {
                mManualEditHitCountdown--;
                if (mManualEditHitCountdown == 0) {
                    mManualEditHitCountdown = TAPS_TO_MANUAL_EDIT;
                    editConfig.launch(new Intent(requireContext(), JsonEditActivity.class)
                            .putExtra(BaseActivity.EXTRA_PATH, path));
                } else if (mManualEditHitCountdown < (TAPS_TO_MANUAL_EDIT - 2)) {
                    if (mManualEditHitToast != null) {
                        mManualEditHitToast.cancel();
                    }
                    mManualEditHitToast = Toast.makeText(requireContext(),
                            requireContext().getResources().getQuantityString(
                                    R.plurals.show_manual_edit_countdown, mManualEditHitCountdown,
                                    mManualEditHitCountdown),
                            Toast.LENGTH_SHORT);
                    mManualEditHitToast.show();
                }
            }
            return true;
        });
    }

    @Override
    public void changeDataSource(String packageName, Uri parentPath, Uri path) {
        super.changeDataSource(packageName, parentPath, path);
    }

    @Override
    protected int getPreferenceResourceId() {
        if (Constants.Nikke.equals(packageName)) {
            return R.xml.preference_nikke;
        } else if (Constants.BlueArchiveJP.equals(packageName)) {
            return R.xml.preference_bluejp;
        } else {
            return R.xml.preference_bluecn;
        }
    }
}
