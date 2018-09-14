package enesates.com.mytravelbook;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> names = new ArrayList<String>();
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Burada menümüzü bağlıyoruz. Bunu da MenuInflater kullanarak yapıyoruz.

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_place,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Burada menüye tıklandığı zaman ne olacağını yazıyoruz.

        if(item.getItemId() == R.id.add_place){
            // Eğer tıklanılan menu add_place menusü ise

            Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
            intent.putExtra("info", "new"); // Bu nereye tıklandığını anlamak için yani menüyemi yoksa eski kayıtlı yere mi
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        try {

            MapsActivity.database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
            Cursor cursor = MapsActivity.database.rawQuery("SELECT * FROM places", null);

            int nameIx = cursor.getColumnIndex("name");
            int latitudeIx = cursor.getColumnIndex("latitude");
            int longitudeIx = cursor.getColumnIndex("longitude");


            while(cursor.moveToNext()) {
                String nameFromDatabase = cursor.getString(nameIx);
                String latitudeFromDatabase = cursor.getString(latitudeIx);
                String longitudeFromDatebase = cursor.getString(longitudeIx);

                names.add(nameFromDatabase);

                Double l1 = Double.parseDouble(latitudeFromDatabase);
                Double l2 = Double.parseDouble(longitudeFromDatebase);
                LatLng locationFromDatabase = new LatLng(l1, l2);
                locations.add(locationFromDatabase);


            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // listView'a tıklandığında ne olacağını yazıyorduk.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("info", "old");// Bu nereye tıklandığını anlamak için yani menüyemi yoksa eski kayıtlı yere mi
                intent.putExtra("position", position);//  (Kaçıncıya tıklandı) -  positon listView da hangi pozisyonda, sırada olduğunu 0. mı 1. mi ... belirtir.
                startActivity(intent);

            }
        });

    }
}
