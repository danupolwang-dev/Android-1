package th.ac.bu.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
<<<<<<< HEAD
=======
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
=======
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

<<<<<<< HEAD
<<<<<<< HEAD
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
<<<<<<<< HEAD:app/src/main/java/th/ac/bu/myapplication/HomeFragment.java
        return inflater.inflate(R.layout.fragment_home, container, false);
========
        return inflater.inflate(R.layout.home, container, false);
>>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d:app/src/main/java/th/ac/bu/myapplication/Home.java
    }
}
=======
=======
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout สำหรับหน้า HomeFragment (สร้างไฟล์ fragment_home.xml ใน res/layout)
        return inflater.inflate(R.layout.home, container, false);
    }
}
<<<<<<< HEAD
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
=======
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
