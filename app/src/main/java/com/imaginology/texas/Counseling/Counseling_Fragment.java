package com.imaginology.texas.Counseling;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Counseling.CounselingDetail.CounselingDetail;
import com.imaginology.texas.Counseling.CreateStudentCounseling.CreateCounselingFragment;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Counseling_Fragment extends Fragment implements CounselingDtoAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {
    private CounselingDtoAdapter counselingDtoAdapter;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private SwipeRefreshLayout swipeRefreshLayout;
    //Endless Scrolling in recyclerview
    private int page = 0;
    private boolean isScrolling = false;
    private int totalItems;
    private int totalVisiableItems;
    private int itemsOutsideScreen;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CouncelingCourseList councelingCourseList;
    private LinearLayout allcourses, today, ignoreDate;
    private ImageView ignoreOption, todayOption, allcourseOption;
    private TextView todayOptionText, ignoreDateOptionText, allCourseOptionText;
    private Integer clickCountForIgnoreDate = 0;
    private FloatingActionButton addStudentCounselBtn;
    private ConstraintLayout rootLayout;
    private LinearLayout linearLayout;
    private List<Long> courseIdsForCounseling = new ArrayList<>();
    private Date date;
    private SimpleDateFormat formatter;
    private String dater;
    private boolean ignoreDater = false;
    private String fromDate;
    private String toDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_counseling, container, false);
        progressBar = view.findViewById(R.id.counseling_list_progressbar);
        courseIdsForCounseling.add((long) 0);
        allcourses = view.findViewById(R.id.allCoursesId);
        today = view.findViewById(R.id.todayId);
        ignoreDate = view.findViewById(R.id.ignoreDateId);
        ignoreOption = view.findViewById(R.id.ignoreOptionID);
        todayOption = view.findViewById(R.id.todayOptionID);
        allcourseOption = view.findViewById(R.id.allCoursesOptionId);
        todayOptionText = view.findViewById(R.id.todayOptionTextID);
        ignoreDateOptionText = view.findViewById(R.id.ignoreDateTextId);
        allCourseOptionText = view.findViewById(R.id.allCourseTextId);
        recyclerView = view.findViewById(R.id.counseling_recycler_view);
        linearLayout = view.findViewById(R.id.linearLayout2);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        apiInterface = ApiClient.getRetrofit(getContext()).create(ApiInterface.class);
        loginInstance = new GetLoginInstanceFromDatabase(getContext()).getLoginInstance();
        counselingDtoAdapter = new CounselingDtoAdapter(R.layout.user_list_adapter, getContext());
        counselingDtoAdapter.setCounselingClickListener(Counseling_Fragment.this);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Students For Counsel");
        swipeRefreshLayout = view.findViewById(R.id.swiperefreshstudent);
        addStudentCounselBtn = view.findViewById(R.id.float_add_counsel_btn);
        swipeRefreshLayout.setOnRefreshListener(this);
        date = Calendar.getInstance().getTime();
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dater = formatter.format(date);
        String[] date = dater.split("\\s");
        String from = date[0];
        fromDate = from + " 00:00:00";
        toDate=from+" 23:59:59";
        rootLayout = view.findViewById(R.id.root_layout);
        recyclerView.setAdapter(counselingDtoAdapter);
        defineView(page,courseIdsForCounseling,fromDate,toDate,ignoreDater);
        getDateForFilter(courseIdsForCounseling);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        addStudentCounselBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootLayout.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                fragmentTransaction.replace(R.id.add_counsel_layout, new CreateCounselingFragment());
                fragmentTransaction.addToBackStack("previous");
                fragmentTransaction.commit();

            }
        });
        ignoreDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickCountForIgnoreDate++;
                if (clickCountForIgnoreDate % 2 == 0) {
                    ignoreOption.setImageResource(R.drawable.ic_off);
                    ignoreDater = false;
                    today.setVisibility(View.VISIBLE);

                } else {
                    ignoreOption.setImageResource(R.drawable.ic_on);
                    ignoreDater = true;
                    today.setVisibility(View.GONE);
                }


            }
        });
        allcourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCourse(view);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItems = layoutManager.getItemCount();
                totalVisiableItems = layoutManager.getChildCount();
                itemsOutsideScreen = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (isScrolling && (totalVisiableItems + itemsOutsideScreen == totalItems)
                        ) {
                    isScrolling = false;
                    page = page + 1;
                    defineView(page, courseIdsForCounseling, dater, dater, ignoreDater);

//                    callCouncelingStudentList.defineView(page, 0, "", true, "");
                }
            }


        });
        return view;
    }

    private void getCourse(View view) {
        councelingCourseList = new CouncelingCourseList();
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenu().add("All Courses");
        councelingCourseList.listOfCourses(popupMenu, apiInterface, loginInstance.getCustomerId(),
                loginInstance.getLoginId(), loginInstance.getToken(), getContext());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String courseName = menuItem.getTitle().toString();
                if (courseName.length()>=5){
                    courseName = menuItem.getTitle().toString().substring(0,5).concat("...");
                }
                allCourseOptionText.setText(courseName);
                if(ignoreDater==true){
                     today.setVisibility(View.GONE);
                    if (courseName.equals("All Courses")) {
                        page=0;
                        courseIdsForCounseling.clear();
                        courseIdsForCounseling.add((long) 0);
                        counselingDtoAdapter.counselingList.clear();
                        defineView(page,courseIdsForCounseling,fromDate,toDate,ignoreDater);
                    } else {
                        page=0;
                        Long courseId = Long.valueOf(String.valueOf(menuItem.getItemId()));
                        courseIdsForCounseling.clear();
                        courseIdsForCounseling.add(courseId);
                        counselingDtoAdapter.counselingList.clear();
                        defineView(page,courseIdsForCounseling,fromDate,toDate,ignoreDater);
                    }
                }else {
                    if (courseName.equals("All Courses")) {
                        courseIdsForCounseling.clear();
                        courseIdsForCounseling.add((long) 0);
                        getDateForFilter(courseIdsForCounseling);
                    } else {
                        Long courseId = Long.valueOf(String.valueOf(menuItem.getItemId()));
                        courseIdsForCounseling.clear();
                        courseIdsForCounseling.add(courseId);
                        todayOptionText.setText("Select Date");
                        getDateForFilter(courseIdsForCounseling);
                    }
                }

                return false;
            }
        });
    }

    private void getDateForFilter(List<Long> courseIdsForCounseling) {
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTodayPopUp(getView().findViewById(R.id.todayOptionID), courseIdsForCounseling);
            }
        });

    }

    public void showTodayPopUp(View view, List<Long> courseIdsForCounseling) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_for_today_option);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.select_date:
                        page=0;
                        todayOptionText.setText("Select Date");
                        counselingDtoAdapter.counselingList.clear();
                        break;
                    case R.id.today:
                        todayOptionText.setText("Today");
                        page=0;
                        counselingDtoAdapter.counselingList.clear();
                        defineView(page, courseIdsForCounseling, fromDate, toDate, ignoreDater);
                        break;

                    case R.id.last_week:
                        Calendar previousWeekDay = Calendar.getInstance();
                        counselingDtoAdapter.counselingList.clear();
                        // Add the -1 in the Calendar of WEEK_OF_YEAR
                        previousWeekDay.add(Calendar.WEEK_OF_YEAR, -1);
                        page=0;
                        String lastWeekDate = formatter.format(previousWeekDay.getTime());
                        todayOptionText.setText(menuItem.getTitle());
                        defineView(page, courseIdsForCounseling, lastWeekDate, toDate, ignoreDater);
                        break;

                    case R.id.last_month:
                        Calendar previousMonthDay = Calendar.getInstance();
                        // Add -1 calendar month in current date object
                        todayOptionText.setText(menuItem.getTitle());
                        page=0;
                        counselingDtoAdapter.counselingList.clear();
                        previousMonthDay.add(Calendar.MONTH, -1);
                        String lastMonthDate = formatter.format(previousMonthDay.getTime());
                        defineView(page, courseIdsForCounseling, lastMonthDate, toDate, ignoreDater);
                        break;
                }
                return false;
            }
        });

    }

    private void defineView(int page, List<Long> courseIds, String fromDate, String toDate, boolean ignoreDate) {
        progressBar.setVisibility(View.VISIBLE);
        Call<CounselingListDto> call = apiInterface.getListOfCounsellings(courseIds,
                loginInstance.getCustomerId(),
                loginInstance.getLoginId(),
                loginInstance.getToken(),
                ignoreDate,
                fromDate,
                page,
                20,
                "id,asc",
                toDate
        );
        call.enqueue(new Callback<CounselingListDto>() {
            @Override
            public void onResponse(Call<CounselingListDto> call, Response<CounselingListDto> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        CounselingListDto counselingListDto = response.body();
                        List<CounselingDetailListDto> listDtos = counselingListDto.getCounselingDetailListDtos();
                        counselingDtoAdapter.addListInCounseling(listDtos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //Maps the error message in ErrorMessageDto
                    progressBar.setVisibility(View.GONE);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onFailure(Call<CounselingListDto> call, Throwable t) {
                Toast.makeText(getContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }
    @Override
    public void itemClicked(View v, CounselingDetailListDto counselingListDto, int position) {
        Intent counselingDetailsIntentActivity = new Intent(getActivity().getApplicationContext(), CounselingDetail.class);
        counselingDetailsIntentActivity.putExtra("selectedCounsellingId", counselingListDto.getId());
        startActivity(counselingDetailsIntentActivity);
    }
    @Override
    public void onRefresh() {
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.content_frame, new Counseling_Fragment());
//        ft.commit();
        swipeRefreshLayout.setRefreshing(false);

    }

}
