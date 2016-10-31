package taylorandtanner.gasscanmk1;
//commit 10/3
import android.content.Intent;
import android.net.Uri;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



import org.w3c.dom.Comment;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button next = (Button) findViewById(R.id.button2);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), OcrCaptureActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
                Log.d(TAG, "Not Updating because user not signed in - will cause NPE");
        else
            testReadDatabase();
    }

    private void addDrawerItems() {
        final String[] osArray = {"Change User", "Settings", "View Logs", "Still Image Test", "Receipt Entry"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //     Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                switch (position) {
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
                        createBlankSlate();
                        break;
                    case 4:
                        myIntent = new Intent(view.getContext(), ReceiptForm.class);
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


    public void testWriteDatabase(View view) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, Green World!");
    }

    static private receiptTotals mainInformation = new receiptTotals("0", "0", "0", "0", "unassigned", "0");

   public Query selectQuery(receiptTotals mainInformation, DatabaseReference receiptRef){
        Query myReceiptQuery;
        if (mainInformation.getKey().equals("unassigned")) {
            myReceiptQuery = receiptRef.orderByKey();
            System.out.println("using orderByKey() because key: " + mainInformation.getKey());
            return myReceiptQuery;
        } else {
            myReceiptQuery = receiptRef.orderByKey().startAt(mainInformation.getKey());
            System.out.println("using startAt() with key: " + mainInformation.getKey());
            return myReceiptQuery;
        }
    }

    public void createBlankSlate(){
        String name = "unassigned";
        String lastKey = "unassigned";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
            Log.d(TAG, "Not Updating because user not signed in - will cause NPE");
        else {
            name = user.getDisplayName();
            System.out.println("Logged in with user name: " + name);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference receiptRef = database.getReference("receipt/" + name + "/");//put inside of else{} to prevent errors when not logged in?
        final DatabaseReference mainRef = database.getReference("main/" + name + "/");  //same as above...

        receiptTotals blankEntry = new receiptTotals("0", "0", "0", "0", "unassigned", "0");
        mainRef.setValue(blankEntry);
    }
    static Boolean isFirstQuery = true;
    public void testReadDatabase(){
    //public void testReadDatabase(View view) {
        String name = "unassigned";
        String lastKey = "unassigned";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
            Log.d(TAG, "Not Updating because user not signed in - will cause NPE");
        else {
            name = user.getDisplayName();
            System.out.println("Logged in with user name: " + name);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference receiptRef = database.getReference("receipt/" + name + "/");//put inside of else{} to prevent errors when not logged in?
        final DatabaseReference mainRef = database.getReference("main/" + name + "/");  //same as above...



        final TextView gallonsTextView = (TextView) findViewById(R.id.gallons);
        // Read from the database
        mainRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String lastKey = dataSnapshot.child("key").getValue().toString();
                if((lastKey != "unassigned") && (lastKey != mainInformation.getKey())) {
                    mainInformation.setKey(lastKey);
                    System.out.println("Last key updated: " + mainInformation.getKey());
                }
                else
                    System.out.println("Last key not updated.");



                String totalMiles = dataSnapshot.child("miles").getValue().toString();
                if((totalMiles != "0") && (totalMiles != mainInformation.getMiles())) {   //may want to add greater than
                    mainInformation.setMiles(totalMiles);
                    System.out.println("total Miles updated: " + mainInformation.getMiles());
                }
                else
                    System.out.println("total Miles not updated.");

                String totalMPG = dataSnapshot.child("mpg").getValue().toString();
                if((totalMPG != "0") && (totalMPG != mainInformation.getMPG())) {   //may want to add greater than
                    mainInformation.setMPG(totalMPG);
                    System.out.println("total MPG updated: " + mainInformation.getMPG());
                }
                else
                    System.out.println("total MPG not updated.");

                String totalPrice = dataSnapshot.child("price").getValue().toString();
                if((totalPrice != "0") && (totalPrice != mainInformation.getPrice())) {   //may want to add greater than
                    mainInformation.setPrice(totalPrice);
                    System.out.println("total Price updated: " + mainInformation.getPrice());
                }
                else
                    System.out.println("total Price not updated.");

                String totalGallons = dataSnapshot.child("gallons").getValue().toString();
                if((totalGallons != "0") && (totalGallons != mainInformation.getGallons())) {   //may want to add greater than
                    mainInformation.setGallons(totalGallons);
                    System.out.println("total Gallons updated: " + mainInformation.getGallons());
                }
                else
                    System.out.println("total Gallons not updated.");

                String totalPriceGal = dataSnapshot.child("priceGal").getValue().toString();
                if((totalPriceGal != "0") && (totalPriceGal != mainInformation.getPriceGal())) {   //may want to add greater than
                    mainInformation.setPriceGal(totalPriceGal);
                    System.out.println("total PriceGal updated: " + mainInformation.getPriceGal());
                }
                else
                    System.out.println("total PriceGal not updated.");

                String deltaPriceGal = dataSnapshot.child("deltaPriceGal").getValue().toString();
                if((deltaPriceGal != "0") && (deltaPriceGal != mainInformation.getDeltaPriceGal())) {   //may want to add greater than
                    mainInformation.setDeltaPriceGal(deltaPriceGal);
                    System.out.println("delta PriceGal updated: " + mainInformation.getDeltaPriceGal());
                }
                else
                    System.out.println("delta PriceGal not updated.");
                
                String deltaGal = dataSnapshot.child("deltaGal").getValue().toString();
                if((deltaGal != "0") && (deltaGal != mainInformation.getDeltaGal())) {   //may want to add greater than
                    mainInformation.setDeltaGal(deltaGal);
                    System.out.println("delta Gal updated: " + mainInformation.getDeltaGal());
                }
                else
                    System.out.println("delta Gal not updated.");

                String deltaMiles = dataSnapshot.child("deltaMiles").getValue().toString();
                if((deltaMiles != "0") && (deltaMiles != mainInformation.getDeltaMiles())) {   //may want to add greater than
                    mainInformation.setDeltaMiles(deltaMiles);
                    System.out.println("delta Miles updated: " + mainInformation.getDeltaMiles());
                }
                else
                    System.out.println("delta Miles not updated.");

                String deltaPrice = dataSnapshot.child("deltaPrice").getValue().toString();
                if((deltaPrice != "0") && (deltaPrice != mainInformation.getDeltaPrice())) {   //may want to add greater than
                    mainInformation.setDeltaPrice(deltaPrice);
                    System.out.println("delta Price updated: " + mainInformation.getDeltaPrice());
                }
                else
                    System.out.println("delta Price not updated.");

                String baseMiles = dataSnapshot.child("baseMiles").getValue().toString();
                if((baseMiles != "0") && (baseMiles != mainInformation.getBaseMiles())) {   //may want to add greater than
                    mainInformation.setBaseMiles(baseMiles);
                    System.out.println("base Miles updated: " + mainInformation.getBaseMiles());
                }
                else
                    System.out.println("base Miles not updated.");

                Query myReceiptQuery = selectQuery(mainInformation, receiptRef);
                myReceiptQuery.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot receiptSnapshot: dataSnapshot.getChildren()){

                            final ReceiptEntry currentReceipt = new ReceiptEntry(receiptSnapshot.child("price").getValue().toString(),
                                    receiptSnapshot.child("gallons").getValue().toString(),
                                    receiptSnapshot.child("priceGal").getValue().toString(),
                                    receiptSnapshot.child("miles").getValue().toString(),
                                    receiptSnapshot.getKey().toString());

                            mainRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!mainInformation.getKey().equals(currentReceipt.getKey())) {
                                        performCalculations(currentReceipt, mainInformation);
                                    }
                                    else{
                                        showToMain(currentReceipt, mainInformation);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                    public void showToMain(ReceiptEntry currentReceipt, receiptTotals mainInformation){
                        //Set Values:
                        mainRef.setValue(mainInformation);

                        //Set text values in main layout
                        final TextView milesTextView = (TextView) findViewById(R.id.miles);
                        final TextView gallonsTextView = (TextView) findViewById(R.id.gallons);

                        float mpg = R.id.miles / R.id.gallons;

                        final TextView mpgTextView = (TextView) findViewById(R.id.mpg);   //holding priceGal for now

                        milesTextView.setText(mainInformation.getMiles());
                        gallonsTextView.setText(mainInformation.getGallons());
                        mpgTextView.setText(
                                Double.toString(
                                        BigDecimal.valueOf(Double.parseDouble(mainInformation.getMPG()))
                                                .setScale(2, RoundingMode.HALF_UP).doubleValue()
                                )
                        );
                    }

                    public void performCalculations(ReceiptEntry currentReceipt, receiptTotals mainInformation){
                      /*  System.out.println("****Current Receipt*****:" +
                        "\nMiles on odometer " + currentReceipt.getMiles() +
                                "\nPrice/gal " + currentReceipt.getPriceGal() +
                                "\nGallons " + currentReceipt.getGallons() +
                                "\nPrice " + currentReceipt.getPrice() +
                                "\nKey " + currentReceipt.getKey());

                        System.out.println("*****Current TOTALS*****:" +
                                "\nMiles " + mainInformation.getMiles() +
                                "\nlast Key " + mainInformation.getKey() +
                                "\ngallons " + mainInformation.getGallons() +
                                "\nprice " + mainInformation.getPrice() +
                                "\npriceGal " + mainInformation.getPriceGal() +
                                "\nMPG " + mainInformation.getMPG() +
                                "\n***Deltas****" +
                                "\ndeltaMiles " + mainInformation.getDeltaMiles() +
                                "\ndeltaPrice " + mainInformation.getDeltaPrice() +
                                "\ndeltaPriceGal " + mainInformation.getDeltaPrice() +
                                "\ndeltaGal " + mainInformation.getDeltaGal() +
                                "\ndeltaMPG " + mainInformation.getMPG() +
                                "\nBase Mileage " + mainInformation.getBaseMiles());
                        */

                        System.out.println("**********************************\n" +
                                            "currentKey: " + currentReceipt.getKey() +
                                            "\nKEY: " + mainInformation.getKey() +
                                            "\n**********************************");
                        //Perform calculations:
                        if(mainInformation.getBaseMiles().equals("0")) {  //Should only set for first ever receipt. After this will have value in DB
                            mainInformation.setBaseMiles(currentReceipt.getMiles());
                            mainInformation.setMiles(currentReceipt.getMiles());
                        }
                        else {
                            System.out.println(currentReceipt.getMiles() + "-" + mainInformation.getMiles());
                            mainInformation.setDeltaMiles( //calculatable miles
                                    Integer.toString(Integer.parseInt(currentReceipt.getMiles()) -
                                            Integer.parseInt(mainInformation.getMiles()))
                            );
                            System.out.println(mainInformation.getDeltaMiles() + "+" + mainInformation.getBaseMiles());
                            mainInformation.setMiles(    //Miles total driven since app.
                                    Integer.toString(Integer.parseInt(mainInformation.getDeltaMiles())+
                                            Integer.parseInt(mainInformation.getMiles()) -
                                            Integer.parseInt(mainInformation.getBaseMiles()))
                            );
                        }

                        if(mainInformation.getKey().equals("unassigned") ||
                                !mainInformation.getKey().equals(currentReceipt.getKey())){
                            mainInformation.setKey(currentReceipt.getKey());
                            System.out.println("TEST: mainInfo key updated to: " + mainInformation.getKey());
                        }

                        mainInformation.setDeltaGal(currentReceipt.getGallons());

                        mainInformation.setGallons(
                                Double.toString(Double.parseDouble(mainInformation.getDeltaGal()) +
                                       Double.parseDouble(mainInformation.getGallons()))
                               // Double.toString(Double.parseDouble())
                        );


                        if(!mainInformation.getDeltaMiles().equals("0") || !mainInformation.getDeltaGal().equals("0")){
                            mainInformation.setDeltaMPG(
                                    Double.toString(Double.parseDouble(mainInformation.getDeltaMiles()) /
                                    Double.parseDouble(mainInformation.getDeltaGal()))
                            );
                        }

                        if(!mainInformation.getMiles().equals("0") || !mainInformation.getGallons().equals("0")){
                            mainInformation.setMPG(
                                    Double.toString(Double.parseDouble(mainInformation.getMiles()) /
                                            Double.parseDouble(mainInformation.getGallons()))
                            );
                        }
                    //Set Values:
                        mainRef.setValue(mainInformation);

                        //Set text values in main layout
                        final TextView milesTextView = (TextView) findViewById(R.id.miles);
                        final TextView gallonsTextView = (TextView) findViewById(R.id.gallons);

                        float mpg = R.id.miles / R.id.gallons;

                        final TextView mpgTextView = (TextView) findViewById(R.id.mpg);   //holding priceGal for now

                        milesTextView.setText(mainInformation.getMiles());
                        gallonsTextView.setText(mainInformation.getGallons());
                        mpgTextView.setText(
                                Double.toString(
                                        BigDecimal.valueOf(Double.parseDouble(mainInformation.getMPG()))
                                        .setScale(2, RoundingMode.HALF_UP).doubleValue()
                                )
                        );





                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://taylorandtanner.gasscanmk1/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://taylorandtanner.gasscanmk1/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


   /* public void testReadDatabase(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mpgRef = database.getReference("receipt/" + user + );
        DatabaseReference milesRef = database.getReference("UID/MainData/Dashboard/Vehicle1/miles");
        DatabaseReference gallonsRef = database.getReference("UID/MainData/Dashboard/Vehicle1/gallons");

        final TextView milesTextView = (TextView) findViewById(R.id.miles);
        // Read from the database
        milesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                milesTextView.setText(value);
            }



            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        final TextView mpgTextView = (TextView) findViewById(R.id.mpg);
        // Read from the database
        mpgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                mpgTextView.setText(value);
            }



            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        final TextView gallonsTextView = (TextView) findViewById(R.id.gallons);
        // Read from the database
        gallonsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                gallonsTextView.setText(value);
            }



            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }*/
}
