package com.hsproject.proximity.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hsproject.proximity.R;
import com.hsproject.proximity.constants.Category;

import java.util.ArrayList;
import java.util.Arrays;

public class FragmentPage1 extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_page_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // title
        final TextView textView_title = (TextView) getView().findViewById(R.id.fragment1_title);

/*        // get our folding cell
        final FoldingCell fc = (FoldingCell) getView().findViewById(R.id.folding_cell);

        // attach click listener to folding cell
        fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.toggle(false);
            }
        });*/

        // refresh layout
        final SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(((MainActivity)getActivity()).refresh_layout);

        // floating action button
        final FloatingActionButton fab = getView().findViewById(R.id.fab_room);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MakeRoomActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        // spinners
        Spinner spiCategory = getView().findViewById(R.id.spiCategory);
        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("전체 카테고리");
        categoryList.addAll(Arrays.asList(Category.CATEGORIES));

        ArrayAdapter<String> spiCategoryArrayAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, categoryList);
        spiCategory.setAdapter(spiCategoryArrayAdapter);

        spiCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).setNearbyRoomCategoryFilter(position-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // spinners
        Spinner spiRange = getView().findViewById(R.id.spiRange);

        ArrayAdapter<String> spiRangeArrayAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, Category.RANGES);
        spiRange.setAdapter(spiRangeArrayAdapter);

        spiRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).setNearbyRoomRangeFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // spinners
        Spinner spiIsFriendJoined = getView().findViewById(R.id.spiIsFriendJoined);

        String[] isFriendJoinedList = {"전체 방","친구가 참여한 방"};
        ArrayAdapter<String> spiIsFriendJoinedArrayAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, isFriendJoinedList);
        spiIsFriendJoined.setAdapter(spiIsFriendJoinedArrayAdapter);

        spiIsFriendJoined.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).setFriendJoinedRoomFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}