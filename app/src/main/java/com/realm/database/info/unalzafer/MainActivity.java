package com.realm.database.info.unalzafer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    EditText etName,etAge;
    Button btnSave;
    Realm realm;
    ListView lvInfo;
    ArrayAdapter<String> arrayAdapter;
    String[] data;
    List<String> stringArrayList=new ArrayList<>();
    private int selectPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName=(EditText)findViewById(R.id.etName);
        etAge=(EditText)findViewById(R.id.etAge);
        btnSave=(Button)findViewById(R.id.btnSave);
        lvInfo=(ListView)findViewById(R.id.lvInfo);


        realm=Realm.getDefaultInstance();

        /*
        //her uygulama başlangıcında datayı sıfırlar
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        */

        //eğer veri var ise gösterir
        showInfo();

        //listedeki elemana tıklayınca siler
        lvInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                delete(i);
                Toast.makeText(getApplicationContext(),i+". seçildi",Toast.LENGTH_LONG).show();
            }
        });

        //listedeki elemana uzun basılı tutunca düzenlemeye geçer
        lvInfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                duzenle(i);
                return true;
            }
        });




    }

    public void Kaydet(View view) {

        if(btnSave.getText().toString().equalsIgnoreCase("Güncelle")){
            update();
        }else {
            //girilen datayı set etme

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {


                    //primary key için otomatik arttırma
                    Number maxId = bgRealm.where(InfoModel.class).max("id");
                    int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;

                    InfoModel duaModel = bgRealm.createObject(InfoModel.class);
                    duaModel.setName(etName.getText().toString());
                    duaModel.setAge(etAge.getText().toString());
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // Kayıt Başarılı Bir Şekilde Gereçekleşti ise
                    showInfo();
                    clearText();
                    Toast.makeText(MainActivity.this, "Kayıt başarılı bir şekilde eklendi.", Toast.LENGTH_SHORT).show();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    // Kayıt Başarısız ise bu bölüm çalıştırılır
                }
            });

        }

    }
    public  void showInfo(){
        //datayı okuma ve listview de gösterme

        stringArrayList.clear();
        RealmResults<InfoModel> realmResults=realm.where(InfoModel.class).findAll();
        int i=0;
        for(InfoModel infoModel:realmResults){
            try {

                stringArrayList.add(infoModel.toString());
                i++;
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }
        arrayAdapter=new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1,stringArrayList);
        lvInfo.setAdapter(arrayAdapter);
        
    }

    //tek tek eleman silmek için. seçileni siler
    private  void delete(final int position) {
        final RealmResults<InfoModel> realmResults=realm.where(InfoModel.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                InfoModel dog = realmResults.get(position);
                dog.deleteFromRealm();
                refreshList();

            }
        });
    }
    private void refreshList() {

        //Listedeki datalarıdan sildiğimizde listview e güncelleme atıyoruz
        RealmResults<InfoModel> realmResults = realm.where(InfoModel.class).findAll();
        stringArrayList.clear();
        for(InfoModel infoModel: realmResults){
            stringArrayList.add(String.valueOf(infoModel));
        }
        arrayAdapter.notifyDataSetChanged();

    }
    private void duzenle(int position) {

        //listedeki herhamgi bir elemana uzun süre basılı tutunca elamnın değerlerini edittextlere yazdırıyoruz
        RealmResults<InfoModel> realmResults = realm.where(InfoModel.class).findAll();
        final InfoModel updateTable = realmResults.get(position);
        screenText(updateTable.getName(),updateTable.getAge());
        btnSave.setText("Güncelle");
        this.selectPos=position;

    }

    private void screenText(String name, String age) {
        etName.setText(name);
        etAge.setText(age);
    }

    //güncelleme bölümü
    private  void update(){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {

                RealmResults<InfoModel> realmResults = Realm.getDefaultInstance().where(InfoModel.class).findAll();
                final InfoModel updateTable = realmResults.get(selectPos);
                updateTable.setName(etName.getText().toString());
                updateTable.setAge(etAge.getText().toString());

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Kayıt başarılı bir şekilde güncellendi.", Toast.LENGTH_SHORT).show();
                refreshList();
                clearText();
                btnSave.setText("Kaydet");

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //edittextleri temizlemek için
    private void clearText(){
        etName.setText("");
        etAge.setText("");
    }


    protected  void  onDestroy() {
        super.onDestroy();
    }
}
