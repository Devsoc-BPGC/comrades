package com.macbitsgoa.comrades.coursematerial;

import android.app.Application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * @author aayush singla
 */

public class MaterialVm extends AndroidViewModel {

    private MaterialRepository mRepository;
    private LiveData<List<CourseMaterial>> materialList;
    private Map<String, LiveData<Integer>> materialCount = new HashMap<>(0);

    public MaterialVm(Application application) {
        super(application);
        mRepository = new MaterialRepository(application);
    }

    public LiveData<Integer> getMaterialCount(String courseId) {
        if (!materialCount.containsKey(courseId)) {
            materialCount.put(courseId, mRepository.getMaterialCount(courseId));
        }
        return materialCount.get(courseId);
    }

    public LiveData<List<CourseMaterial>> getMaterialListByName(String courseid) {
        materialList = mRepository.getAllMaterialByName(courseid);
        return materialList;
    }

    public LiveData<List<CourseMaterial>> getMaterialListBySize(String courseid) {
        materialList = mRepository.getAllMaterialBySize(courseid);
        return materialList;
    }

    public LiveData<List<CourseMaterial>> getMaterialListByFileType(String courseid) {
        materialList = mRepository.getAllMaterialByFileType(courseid);
        return materialList;
    }

    public void insert(CourseMaterial courseMaterial) {
        mRepository.insert(courseMaterial);
    }

    public void delete(CourseMaterial courseMaterial) {
        mRepository.delete(courseMaterial);
    }

    public void update(CourseMaterial courseMaterial) {
        mRepository.update(courseMaterial);
    }


}
