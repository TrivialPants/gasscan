package taylorandtanner.gasscanmk1;
//commit 10/3
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//comment so i can commit

public class ReceiptForm extends AppCompatActivity {
    //*******Firebase Declarations*************

    //*********************************************************
    //*******Receipt Text Values*******************************
    private EditText mPrice;
    private EditText mGallons;
    private EditText mPriceGal;
    private EditText mMiles;

    private static final String TAG = "ReceiptForm";
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_form);

        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mPrice = (EditText) findViewById(R.id.receiptPriceText);          //Initialize to NULL to prevent errors later
        mGallons = (EditText) findViewById(R.id.receiptGallonsText);
        mPriceGal = (EditText) findViewById(R.id.receiptPriceGalText);
        mMiles = (EditText)findViewById(R.id.receiptMilesText);

        ReceiptEntry inputReceipt =     //Receipt from ocr output
        (ReceiptEntry)getIntent().getSerializableExtra(OcrCaptureActivity.SER_KEY);

        if(inputReceipt != null) {
            mPrice.setText(inputReceipt.getPrice());
            mGallons.setText(inputReceipt.getGallons());
            mPriceGal.setText(inputReceipt.getPriceGal());
            mMiles.setText(inputReceipt.getMiles());
        }

    }


    private void addDrawerItems() {
        final String[] osArray = { "Change User", "Settings", "View Logs", "Clear Totals (Firebase)", "Main Menu" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //     Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                switch(position) {
                    case 0:
                        Intent myIntent = new Intent(view.getContext(), SignInActivity.class);
                        startActivityForResult(myIntent, 0);
                        break;
                    case 1:
                        myIntent = new Intent(view.getContext(), Settings.class);
                        startActivityForResult(myIntent, 0);
                        break;
                    case 2:
                        myIntent = new Intent(view.getContext(), ViewLogs.class);
                        startActivityForResult(myIntent, 0);
                        break;
                    case 3:
                       // myIntent = new Intent(view.getContext(), ImageSelect.class);
                       // startActivityForResult(myIntent, 0);
                        break;
                    case 4:
                        myIntent = new Intent(view.getContext(), MainActivity.class);
                        startActivityForResult(myIntent, 0);
                        break;
                    default:
                        return;
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Options");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //  return true;
        //}

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void pushReceipt(final View view) {
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("/receipt/" + name + "/");
        DatabaseReference mainRef = database.getReference("/main/" + name + "/");
        //Assign values from layout to placeholder variables:
        //Integer maxMiles = Integer.parseInt(mainRef.child("miles").toString()) +
        //            Integer.parseInt(mainRef.child("baseMiles").toString());


        final String[] maxMiles1 = {"unassigned"};
        final String[] maxMiles2 = {"unassigned"};

        mainRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                maxMiles1[0] = dataSnapshot.child("miles").getValue().toString();
                maxMiles2[0] = dataSnapshot.child("baseMiles").getValue().toString();

                pushToFB(maxMiles1[0], maxMiles2[0]);
            }



            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }

            public void pushToFB(String maxMiles1, String maxMiles2){
                //if(Integer.parseInt(mMiles.getText().toString()) > maxMiles){
                System.out.println("Breach: " + maxMiles1 + "+" + maxMiles2 + " is less than " + mMiles.getText());

                ReceiptEntry receiptEntry = new
                        ReceiptEntry(mPrice.getText().toString(),
                        mGallons.getText().toString(),
                        mPriceGal.getText().toString(),
                        mMiles.getText().toString(),
                        "unassigned");

                myRef.push().setValue(receiptEntry);
                //GO BACK TO MAIN MENU:
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

       /* //if(Integer.parseInt(mMiles.getText().toString()) > maxMiles){
            System.out.println("Breach: " + maxMiles1[0] + "+" + maxMiles2[0] + " is less than " + mMiles.getText());

        ReceiptEntry receiptEntry = new
                ReceiptEntry(mPrice.getText().toString(),
                            mGallons.getText().toString(),
                            mPriceGal.getText().toString(),
                            mMiles.getText().toString(),
                            "unassigned");

                myRef.push().setValue(receiptEntry);
        //GO BACK TO MAIN MENU:
        Intent myIntent = new Intent(view.getContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);*/
    }

}
