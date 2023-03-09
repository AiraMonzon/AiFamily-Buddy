package monzonation.aifamilybuddy;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardFragment extends Fragment implements BalanceAdapter.ItemClickListener{

    View view;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    ArrayList<String> listDate = new ArrayList<>();
    ArrayList<String> listAmount = new ArrayList<>();
    ArrayList<String> listAmountExpenses = new ArrayList<>();
    private String getCurrentYearAndMonthTitle(){
        return new SimpleDateFormat("yyyy MMMM", Locale.getDefault()).format(new Date());
    }
    private String getCurrentYearAndMonth(){
        return new SimpleDateFormat("yyyy/MM", Locale.getDefault()).format(new Date());
    }
    TextView textViewIncomeAmount, textViewBalanceAmount, textViewExpenseAmount, textViewMonth, textViewMessage;
    int sum = 0;
    int sumExpenses = 0;
    RecyclerView recyclerView;
    List<BalanceModel> balanceModelList = new ArrayList<>();
    BalanceAdapter balanceAdapter;
    long idLong;
    ProgressBar progressBar;
    DashboardFragment context = this;
    private static final int STORAGE_PERMISSION_CODE = 101;
    ArrayList<String> listNameForExcel = new ArrayList<>();
    ArrayList<String> listAmountForExcel = new ArrayList<>();
    ArrayList<String> listDateForExcel = new ArrayList<>();
    ArrayList<String> listDetailsForExcel = new ArrayList<>();
    ArrayList<String> listNameExpenseForExcel = new ArrayList<>();
    ArrayList<String> listAmountExpenseForExcel = new ArrayList<>();
    ArrayList<String> listDateExpenseForExcel = new ArrayList<>();
    ArrayList<String> listDetailsExpenseForExcel = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        textViewIncomeAmount = view.findViewById(R.id.textViewIncomeAmount);
        textViewExpenseAmount = view.findViewById(R.id.textViewExpenseAmount);
        textViewBalanceAmount = view.findViewById(R.id.textViewBalanceAmount);
        textViewMonth = view.findViewById(R.id.textViewMonth);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));
        textViewMonth.setText(getCurrentYearAndMonthTitle());

        textViewMessage = view.findViewById(R.id.textViewMessage);

        CollectionReference collectionReferenceMessage = firebaseFirestore.collection("Message Demo");
        collectionReferenceMessage.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int incomeSize = task.getResult().size();
                                if(incomeSize > 0){
                                    String message = document.getString("message");
                                    textViewMessage.setText(message);
                                }
                            }

                        } else {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
                        }

                    }
                });

        textViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                {
                    return;
                }
                mLastClickTime=SystemClock.elapsedRealtime();
                //start Dialog
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.note_dashboard);
                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                EditText editTextMessage = dialog.findViewById(R.id.editTextMessage);
                Button buttonSave = dialog.findViewById(R.id.buttonSave);
                progressBar = dialog.findViewById(R.id.progressBar);

                buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                        {
                            return;
                        }
                        mLastClickTime=SystemClock.elapsedRealtime();

                        String message = editTextMessage.getText().toString().trim();

                        if(message.isEmpty())
                        {
                            editTextMessage.setError("Message is required!");
                            editTextMessage.requestFocus();
                            return;
                        }

                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setIndeterminate(true);
                        progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));

                        // Create a new user with a first and last name
                        Map<String, Object> user = new HashMap<>();
                        user.put("message", message);

                        DocumentReference documentReference = firebaseFirestore.collection("Message Demo").document("message");
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressBar.setVisibility(View.GONE);
                                textViewMessage.setText(message);
                                Toast.makeText(getActivity(), "Message Sent!", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error: "+e, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });


            }
        });

        CollectionReference collectionReference = firebaseFirestore.collection("Income Demo");
        collectionReference.orderBy("amount", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String pattern = "yyyy/MM";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int incomeSize = task.getResult().size();
                                if(incomeSize > 0){
                                    String amount = document.getString("amount");
                                    String date = document.getString("date");
                                    try {
                                        Date dateCurrentYearMonth = simpleDateFormat.parse(getCurrentYearAndMonth());
                                        assert date != null;
                                        Date dateIncomeYearMonth = simpleDateFormat.parse(date);
                                        assert dateIncomeYearMonth != null;
                                        String StringDateIncomeYearMonth = simpleDateFormat.format(dateIncomeYearMonth);
                                        assert dateCurrentYearMonth != null;
                                        String StringDateCurrentYearMonth = simpleDateFormat.format(dateCurrentYearMonth);
                                        if(StringDateCurrentYearMonth.equals(StringDateIncomeYearMonth)){
                                            listDate.add(date);
                                            listAmount.add(amount);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    
                                }
                            }


                            CollectionReference collectionReferenceExpenses = firebaseFirestore.collection("Expenses Demo");
                            collectionReferenceExpenses.orderBy("amount", Query.Direction.DESCENDING).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                String pattern = "yyyy/MM";
                                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    int incomeSize = task.getResult().size();
                                                    if(incomeSize > 0){
                                                        String amount = document.getString("amount");
                                                        String date = document.getString("date");
                                                        try {
                                                            Date dateCurrentYearMonth = simpleDateFormat.parse(getCurrentYearAndMonth());
                                                            assert date != null;
                                                            Date dateIncomeYearMonth = simpleDateFormat.parse(date);
                                                            assert dateIncomeYearMonth != null;
                                                            String StringDateIncomeYearMonth = simpleDateFormat.format(dateIncomeYearMonth);
                                                            assert dateCurrentYearMonth != null;
                                                            String StringDateCurrentYearMonth = simpleDateFormat.format(dateCurrentYearMonth);
                                                            if(StringDateCurrentYearMonth.equals(StringDateIncomeYearMonth)){
                                                                listAmountExpenses.add(amount);
                                                            }
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                listMonthlyBalance(listAmount, listAmountExpenses);
                                            } else {
                                                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
                        }

                    }
                });

        CollectionReference collectionReferenceBalance = firebaseFirestore.collection("Balance Demo");
        collectionReferenceBalance.orderBy("date", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                int balanceSize = task.getResult().size();
                                if(balanceSize > 0){
                                    String id = document.getString("id");
                                    String name = document.getString("name");
                                    String date = document.getString("date");
                                    long balance = document.getLong("balance");
                                    long expense = document.getLong("total expense");
                                    long income = document.getLong("total income");
                                    BalanceModel balanceModel = new BalanceModel();
                                    balanceModel.setId(id);
                                    balanceModel.setName(name);
                                    balanceModel.setDate(date);
                                    balanceModel.setBalance(balance);
                                    balanceModel.setIncome(income);
                                    balanceModel.setExpense(expense);

                                    balanceModelList.add(balanceModel);

                                }else{
                                    Toast.makeText(getActivity(), "No Records Found", Toast.LENGTH_LONG).show();
                                }



                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
                        }
                        list(balanceModelList);
                    }
                });

        return view;
    }

    private long mLastClickTime =0;

    private void listMonthlyBalance(ArrayList<String> listAmount,ArrayList<String> listAmountExpenses) {

        for(int i=0; i<listAmount.size(); i++){
            sum += Integer.parseInt(listAmount.get(i));
        }
        String incomeSum = String.valueOf(sum);
        textViewIncomeAmount.setText(incomeSum);

        for(int i=0; i<listAmountExpenses.size(); i++){
            sumExpenses += Integer.parseInt(listAmountExpenses.get(i));
        }
        String ExpensesSumString = String.valueOf(sumExpenses);
        textViewExpenseAmount.setText(ExpensesSumString);

        int balance = sum - sumExpenses;
        String balanceString = String.valueOf(balance);
        textViewBalanceAmount.setText(balanceString);

        textViewBalanceAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("id", getCurrentYearAndMonthTitle());
                user.put("name", getCurrentYearAndMonthTitle());
                user.put("balance", balance);
                user.put("date", getCurrentYearAndMonth());
                user.put("total income", sum);
                user.put("total expense", sumExpenses);

                DocumentReference documentReference = firebaseFirestore.collection("Balance Demo").document(getCurrentYearAndMonthTitle());
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Successfully added!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void list(List<BalanceModel> balanceModelList) {
        if(balanceModelList.isEmpty()){
            progressBar.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            balanceAdapter = new BalanceAdapter(getContext(), balanceModelList);
            balanceAdapter.setClickListener(context);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(balanceAdapter);
//            long lastId = Long.parseLong(incomeAdapter.getLastId().IdData());
//            idLong = incomeAdapter.getLastId().getId();
//            idLong = Long.parseLong(balanceAdapter.getLastIdReversed().getId());
        }
    }
    @Override
    public void onItemClickExport(View view, int position) {
        if(SystemClock.elapsedRealtime() - mLastClickTime < 1000)
        {
            return;
        }
        mLastClickTime=SystemClock.elapsedRealtime();

        String dateGetString = balanceAdapter.getDataDate(position);
        String nameGetString = balanceAdapter.getDataName(position);
        long incomeGetString = balanceAdapter.getDataIncome(position);
        long expenseGetString = balanceAdapter.getDataExpense(position);
        long balanceGetString = balanceAdapter.getDataBalance(position);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE, dateGetString, nameGetString, incomeGetString,expenseGetString,balanceGetString);
    }

    public void checkPermission(String permission, int requestCode, String dateGetString, String nameGetString, long  incomeGetString, long expenseGetString, long balanceGetString) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        } else {
            listNameForExcel.clear();
            listAmountForExcel.clear();
            listDateForExcel.clear();
            listDetailsForExcel.clear();
            listNameExpenseForExcel.clear();
            listAmountExpenseForExcel.clear();
            listDateExpenseForExcel.clear();
            listDetailsExpenseForExcel.clear();

            CollectionReference collectionReference = firebaseFirestore.collection("Income Demo");
            collectionReference.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String pattern = "yyyy/MM";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());

                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    int lol = task.getResult().size();
                                    if(lol > 0){
                                        String name = document.getString("name");
                                        String amount = document.getString("amount");
                                        String date = document.getString("date");
                                        String details = document.getString("details");
                                        try {
                                            Date dateCurrentYearMonth = simpleDateFormat.parse(dateGetString);
                                            assert date != null;
                                            Date dateIncomeYearMonth = simpleDateFormat.parse(date);
                                            assert dateIncomeYearMonth != null;
                                            String StringDateIncomeYearMonth = simpleDateFormat.format(dateIncomeYearMonth);
                                            assert dateCurrentYearMonth != null;
                                            String StringDateCurrentYearMonth = simpleDateFormat.format(dateCurrentYearMonth);
                                            if(StringDateCurrentYearMonth.equals(StringDateIncomeYearMonth)){
                                                listNameForExcel.add(name);
                                                listAmountForExcel.add(amount);
                                                listDateForExcel.add(date);
                                                listDetailsForExcel.add(details);
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
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
                                                            try {
                                                                Date dateCurrentYearMonth = simpleDateFormat.parse(dateGetString);
                                                                assert date != null;
                                                                Date dateIncomeYearMonth = simpleDateFormat.parse(date);
                                                                assert dateIncomeYearMonth != null;
                                                                String StringDateIncomeYearMonth = simpleDateFormat.format(dateIncomeYearMonth);
                                                                assert dateCurrentYearMonth != null;
                                                                String StringDateCurrentYearMonth = simpleDateFormat.format(dateCurrentYearMonth);
                                                                if(StringDateCurrentYearMonth.equals(StringDateIncomeYearMonth)){
                                                                    listNameExpenseForExcel.add(name);
                                                                    listAmountExpenseForExcel.add(amount);
                                                                    listDateExpenseForExcel.add(date);
                                                                    listDetailsExpenseForExcel.add(details);
                                                                }
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
                                                }

                                                Workbook wb = new HSSFWorkbook();
                                                Cell cell = null;
                                                CellStyle cellStyle = wb.createCellStyle();
                                                cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

                                                Sheet sheet = null;
                                                sheet = wb.createSheet("Income");

                                                Row rowTitle = sheet.createRow(0);

                                                cell = rowTitle.createCell(0);
                                                cell.setCellValue("Name");
                                                cell.setCellStyle(cellStyle);

                                                cell = rowTitle.createCell(1);
                                                cell.setCellValue("Amount");
                                                cell.setCellStyle(cellStyle);

                                                cell = rowTitle.createCell(2);
                                                cell.setCellValue("Date");
                                                cell.setCellStyle(cellStyle);

                                                cell = rowTitle.createCell(3);
                                                cell.setCellValue("Details");
                                                cell.setCellStyle(cellStyle);

                                                cell = rowTitle.createCell(4);
                                                cell.setCellValue("Total Income");
                                                cell.setCellStyle(cellStyle);

                                                cell = rowTitle.createCell(5);
                                                cell.setCellValue(incomeGetString);
                                                cell.setCellStyle(cellStyle);

                                                for (int i = 1; i < listNameForExcel.size() + 1; i++) {
                                                    Row rowIncome = sheet.createRow(i);
                                                    cell = rowIncome.createCell(0);
                                                    String valueName = listNameForExcel.get(i - 1);
                                                    cell.setCellValue(valueName);
                                                    cell = rowIncome.createCell(1);
                                                    String valueAmount = listAmountForExcel.get(i - 1);
                                                    cell.setCellValue(valueAmount);
                                                    cell = rowIncome.createCell(2);
                                                    String valueDate = listDateForExcel.get(i - 1);
                                                    cell.setCellValue(valueDate);
                                                    cell = rowIncome.createCell(3);
                                                    String valueDetails = listDetailsForExcel.get(i - 1);
                                                    cell.setCellValue(valueDetails);
                                                }
                                                Cell cellExpense = null;
                                                CellStyle cellStyleExpense = wb.createCellStyle();
                                                cellStyleExpense.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

                                                Sheet sheetExpense = null;
                                                sheetExpense = wb.createSheet("Expense");

                                                Row rowTitleExpense = sheetExpense.createRow(0);

                                                cellExpense = rowTitleExpense.createCell(0);
                                                cellExpense.setCellValue("Name");
                                                cellExpense.setCellStyle(cellStyle);

                                                cellExpense = rowTitleExpense.createCell(1);
                                                cellExpense.setCellValue("Amount");
                                                cellExpense.setCellStyle(cellStyle);

                                                cellExpense = rowTitleExpense.createCell(2);
                                                cellExpense.setCellValue("Date");
                                                cellExpense.setCellStyle(cellStyle);

                                                cellExpense = rowTitleExpense.createCell(3);
                                                cellExpense.setCellValue("Details");
                                                cellExpense.setCellStyle(cellStyle);

                                                cellExpense = rowTitleExpense.createCell(4);
                                                cellExpense.setCellValue("Total Expense");
                                                cellExpense.setCellStyle(cellStyle);

                                                cellExpense = rowTitleExpense.createCell(5);
                                                cellExpense.setCellValue(expenseGetString);
                                                cellExpense.setCellStyle(cellStyle);

                                                for (int i = 1; i < listNameExpenseForExcel.size() + 1; i++) {
                                                    Row rowExpense = sheetExpense.createRow(i);
                                                    cellExpense = rowExpense.createCell(0);
                                                    String valueName = listNameExpenseForExcel.get(i - 1);
                                                    cellExpense.setCellValue(valueName);
                                                    cellExpense = rowExpense.createCell(1);
                                                    String valueAmount = listAmountExpenseForExcel.get(i - 1);
                                                    cellExpense.setCellValue(valueAmount);
                                                    cellExpense = rowExpense.createCell(2);
                                                    String valueDate = listDateExpenseForExcel.get(i - 1);
                                                    cellExpense.setCellValue(valueDate);
                                                    cellExpense = rowExpense.createCell(3);
                                                    String valueDetails = listDetailsExpenseForExcel.get(i - 1);
                                                    cellExpense.setCellValue(valueDetails);
                                                }

                                                Cell cellBalance = null;
                                                CellStyle cellStyleBalance = wb.createCellStyle();
                                                cellStyleBalance.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

                                                Sheet sheetBalance = null;
                                                sheetBalance = wb.createSheet("Balance");

                                                Row rowTitleBalance = sheetBalance.createRow(0);

                                                cellBalance = rowTitleBalance.createCell(0);
                                                cellBalance.setCellValue("Total Balance");
                                                cellBalance.setCellStyle(cellStyle);

                                                Row rowTitleBalanceBelow = sheetBalance.createRow(1);

                                                cellBalance = rowTitleBalanceBelow.createCell(0);
                                                cellBalance.setCellValue(balanceGetString);
                                                cellBalance.setCellStyle(cellStyle);

                                                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" +nameGetString +" by AiFamilyBuddy.xls");
                                                FileOutputStream outputStream = null;
                                                try {
                                                    outputStream = new FileOutputStream(file);
                                                    wb.write(outputStream);
                                                    Toast.makeText(getActivity(), "Saved as "+nameGetString +" by AiFamilyBuddy.xls", Toast.LENGTH_LONG).show();
                                                } catch (java.io.IOException e) {
                                                    e.printStackTrace();
                                                    try {
                                                        outputStream.close();
                                                    } catch (IOException ex) {
                                                        ex.printStackTrace();
                                                    }

                                                }

                                            }
                                        });


                            } else {
                                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }
    }
}
