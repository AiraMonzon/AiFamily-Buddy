package monzonation.aifamilybuddy;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ExpensesFragment extends Fragment implements ExpensesAdapter.ItemClickListener{

    View view;
    EditText editTextName, editTextAmount, editTextDate, editTextDetails;
    TextView textViewName, textViewAmount, textViewDate, textViewDetails, textViewIncome;
    Button buttonSave, buttonDelete;
    RecyclerView recyclerView;
    List<IncomeModel> incomeModelList = new ArrayList<>();
    ExpensesAdapter incomeAdapter;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    ExpensesFragment context = this;
    String StringName, StringAmount, StringDate, StringDetails;
    long idLong;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_expenses, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

        CollectionReference collectionReference = firebaseFirestore.collection("Expenses Demo");
        collectionReference.orderBy("id", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                int lol = task.getResult().size();
                                if(lol > 0){
                                    long id = document.getLong("id");
                                    String name = document.getString("name");
                                    String amount = document.getString("amount");
                                    String date = document.getString("date");
                                    String details = document.getString("details");
                                    IncomeModel incomeModel = new IncomeModel();
                                    incomeModel.setId(id);
                                    incomeModel.setName(name);
                                    incomeModel.setAmount(amount);
                                    incomeModel.setDate(date);
                                    incomeModel.setDetails(details);
                                    incomeModelList.add(incomeModel);
                                }else{
                                    IncomeModel incomeModel = new IncomeModel();
                                    incomeModelList.add(incomeModel);
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
                        }
                        list(incomeModelList);
                    }
                });
        setHasOptionsMenu(true);
        return view;
    }

    private long mLastClickTime =0;
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            //start Dialog
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.income_update_list);
            dialog.show();

            Window window = dialog.getWindow();
            window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

            editTextName = dialog.findViewById(R.id.editTextName);
            editTextAmount = dialog.findViewById(R.id.editTextAmount);
            editTextDate = dialog.findViewById(R.id.editTextDate);
            editTextDetails = dialog.findViewById(R.id.editTextDetails);
            buttonSave = dialog.findViewById(R.id.buttonSave);
            progressBar = dialog.findViewById(R.id.progressBar);
            textViewIncome = dialog.findViewById(R.id.textViewIncome);

            String expense = "Expenses";
            textViewIncome.setText(expense);

            editTextDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    DatePickerDialog picker = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    String selectDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                                    editTextDate.setText(selectDate);
                                }
                            }, year, month, day);
                    picker.show();
                }
            });
            editTextDate.setInputType(InputType.TYPE_NULL);
            editTextDate.setFocusable(false);
            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    StringName = editTextName.getText().toString().trim();
                    StringAmount = editTextAmount.getText().toString().trim();
                    StringDate = editTextDate.getText().toString().trim();
                    StringDetails = editTextDetails.getText().toString().trim();
                    long idLongInc = idLong + 1;
                    String idLongString = Long.toString(idLongInc);

                    if (StringName.isEmpty()) {
                        editTextName.setError("Name is required!");
                        editTextName.requestFocus();
                        return;
                    }
                    if (!StringName.matches("[a-zA-Z\\s]+")) {
                        editTextName.setError("Letters only!");
                        editTextName.requestFocus();
                        return;
                    }
                    if (StringAmount.isEmpty()) {
                        editTextAmount.setError("Amount is required!");
                        editTextAmount.requestFocus();
                        return;
                    }
                    if (!StringAmount.matches("\\d+")) {
                        editTextAmount.setError("Numbers Only!");
                        editTextAmount.requestFocus();
                        return;
                    }
                    if (StringDate.isEmpty()) {
                        editTextDate.setError("Date is required!");
                        editTextDate.requestFocus();
                        return;
                    }
                    editTextDate.setError(null);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

                    // Create a new user with a first and last name
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", idLongInc);
                    user.put("name", StringName);
                    user.put("amount", StringAmount);
                    user.put("date", StringDate);
                    user.put("details", StringDetails);

                    DocumentReference documentReference = firebaseFirestore.collection("Expenses Demo").document(idLongString);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressBar.setVisibility(View.GONE);
                            assert getActivity() != null;
                            Toast.makeText(getActivity(), "Successfully added!", Toast.LENGTH_LONG).show();
                            IncomeModel incomeModel = new IncomeModel(idLongInc, StringName, StringAmount, StringDate, StringDetails);
                            incomeModelList.add(0, incomeModel);
                            list(incomeModelList);
                            dialog.dismiss();
                        }
                    });
                }
            });
            return true;
        }// If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onItemClickDelete(View view, int position) {

        if(SystemClock.elapsedRealtime() - mLastClickTime < 1000)
        {
            return;
        }
        mLastClickTime=SystemClock.elapsedRealtime();
        //start Dialog
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.income_delete_list);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        String idLongGetString = String.valueOf(incomeAdapter.getDataId(position));
        String nameGetString = incomeAdapter.getDataName(position);
        String amountGetString = incomeAdapter.getDataAmount(position);
        String dateGetString = incomeAdapter.getDataDate(position);
        String detailsGetString = incomeAdapter.getDataDetails(position);

        textViewName = dialog.findViewById(R.id.textViewName);
        textViewAmount = dialog.findViewById(R.id.textViewAmount);
        textViewDate = dialog.findViewById(R.id.textViewDate);
        textViewDetails = dialog.findViewById(R.id.textViewDetails);

        String nameGetStringSet = "Name: "+ nameGetString;
        String amountGetStringSet = "Amount: "+ amountGetString;
        String dateGetStringSet = "Date: "+ dateGetString;
        String detailsGetStringSet = "Details: "+ detailsGetString;

        textViewName.setText(nameGetStringSet);
        textViewAmount.setText(amountGetStringSet);
        textViewDate.setText(dateGetStringSet);
        textViewDetails.setText(detailsGetStringSet);

        buttonDelete = dialog.findViewById(R.id.buttonDelete);
        progressBar = dialog.findViewById(R.id.progressBar);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                {
                    return;
                }
                mLastClickTime=SystemClock.elapsedRealtime();
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

                firebaseFirestore.collection("Expenses Demo").document(idLongGetString)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);
                                assert getActivity() != null;
                                Toast.makeText(getContext(), "Successfully deleted!", Toast.LENGTH_LONG).show();
                                incomeModelList.remove(position);
                                incomeAdapter.notifyItemRemoved(position);
                                list(incomeModelList);
                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                assert getActivity() != null;
                                Toast.makeText(getContext(), "Error: "+e, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
    @Override
    public void onItemClickUpdate(View view, int position) {
        if(SystemClock.elapsedRealtime() - mLastClickTime < 1000)
        {
            return;
        }
        mLastClickTime=SystemClock.elapsedRealtime();
        //start Dialog
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.income_update_list);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        String idLongGetString = String.valueOf(incomeAdapter.getDataId(position));
        String nameGetString = incomeAdapter.getDataName(position);
        String amountGetString = incomeAdapter.getDataAmount(position);
        String dateGetString = incomeAdapter.getDataDate(position);
        String detailsGetString = incomeAdapter.getDataDetails(position);
        String update = "Update this record?";
        String updateBtnString = "Update";

        textViewIncome = dialog.findViewById(R.id.textViewIncome);
        editTextName = dialog.findViewById(R.id.editTextName);
        editTextAmount = dialog.findViewById(R.id.editTextAmount);
        editTextDate = dialog.findViewById(R.id.editTextDate);
        editTextDetails = dialog.findViewById(R.id.editTextDetails);
        buttonSave = dialog.findViewById(R.id.buttonSave);
        progressBar = dialog.findViewById(R.id.progressBar);

        textViewIncome.setText(update);
        editTextName.setText(nameGetString);
        editTextAmount.setText(amountGetString);
        editTextDate.setText(dateGetString);
        editTextDetails.setText(detailsGetString);
        buttonSave.setText(updateBtnString);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                {
                    return;
                }
                mLastClickTime=SystemClock.elapsedRealtime();

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String selectDate =year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                                editTextDate.setText(selectDate);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        editTextDate.setInputType(InputType.TYPE_NULL);
        editTextDate.setFocusable(false);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                {
                    return;
                }
                mLastClickTime=SystemClock.elapsedRealtime();

                StringName = editTextName.getText().toString().trim();
                StringAmount = editTextAmount.getText().toString().trim();
                StringDate = editTextDate.getText().toString().trim();
                StringDetails = editTextDetails.getText().toString().trim();
                long idLongCovert = Long.parseLong(idLongGetString);

                if(StringName.isEmpty())
                {
                    editTextName.setError("Name is required!");
                    editTextName.requestFocus();
                    return;
                }
                if (!StringName.matches("[a-zA-Z\\s]+")) {
                    editTextName.setError("Letters only!");
                    editTextName.requestFocus();
                    return;
                }
                if(StringAmount.isEmpty())
                {
                    editTextAmount.setError("Amount is required!");
                    editTextAmount.requestFocus();
                    return;
                }
                if (!StringAmount.matches("\\d+")) {
                    editTextAmount.setError("Numbers Only!");
                    editTextAmount.requestFocus();
                    return;
                }
                if(StringDate.isEmpty())
                {
                    editTextDate.setError("Date is required!");
                    editTextDate.requestFocus();
                    return;
                }
                editTextDate.setError(null);

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("id", idLongCovert);
                user.put("name", StringName);
                user.put("amount", StringAmount);
                user.put("date", StringDate);
                user.put("details", StringDetails);

                DocumentReference documentReference = firebaseFirestore.collection("Expenses Demo").document(idLongGetString);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.GONE);
                        assert getActivity() != null;
                        Toast.makeText(getActivity(), "Successfully Updated!", Toast.LENGTH_LONG).show();
                        IncomeModel incomeModel = new IncomeModel(idLongCovert, StringName, StringAmount,StringDate, StringDetails);
                        incomeModelList.set(position,incomeModel);
                        list(incomeModelList);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        assert getActivity() != null;
                        Toast.makeText(getContext(), "Error: "+e, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void list(List<IncomeModel> incomeModelList) {
        if(incomeModelList.isEmpty()){
            progressBar.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            incomeAdapter = new ExpensesAdapter(getContext(), incomeModelList);
            incomeAdapter.setClickListener(context);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(incomeAdapter);
            idLong = incomeAdapter.getLastIdReversed().getId();
        }
    }
}
