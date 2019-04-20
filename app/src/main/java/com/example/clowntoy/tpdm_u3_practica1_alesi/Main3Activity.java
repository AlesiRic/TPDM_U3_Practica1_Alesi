package com.example.clowntoy.tpdm_u3_practica1_alesi;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {

    EditText nameS,company,year,genre;
    Button ins,del,upt,cons;
    ListView lista;
    DatabaseReference database;
    List<Serie> datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        nameS=findViewById(R.id.nSerie);
        company=findViewById(R.id.cSerie);
        year=findViewById(R.id.aSerie);
        genre=findViewById(R.id.gSerie);

        ins=findViewById(R.id.insSerie);
        del=findViewById(R.id.eliSerie);
        upt=findViewById(R.id.actSerie);
        cons=findViewById(R.id.conSerie);

        lista=findViewById(R.id.listaSerie);

        database=FirebaseDatabase.getInstance().getReference();

        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertar();
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar();
            }
        });

        cons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultar();
            }
        });

        upt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar();
            }
        });

        database.child("series").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datos = new ArrayList<>();

                if(dataSnapshot.getChildrenCount()<=0){
                    Toast.makeText(Main3Activity.this, "ERROR: No hay datos", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(final DataSnapshot snap : dataSnapshot.getChildren()){
                    database.child("series").child(snap.getKey()).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Serie s = dataSnapshot.getValue(Serie.class);

                                    if(s!=null){
                                        datos.add(s);
                                    }
                                    cargarSelect();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void cargarSelect(){
        if (datos.size()==0) return;
        String nombres[] = new String[datos.size()];

        for(int i = 0; i<nombres.length; i++){
            Serie s = datos.get(i);
            nombres[i] = s.nombre;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
        lista.setAdapter(adapter);
    }


    private void insertar(){
        if(nameS.getText().toString().equals("") ||
                company.getText().toString().equals("") ||
                year.getText().toString().equals("") ||
                genre.getText().toString().equals("")){
            Toast.makeText(Main3Activity.this,"No puede dejar campos vacios",Toast.LENGTH_SHORT).show();
            return;
        }

        Serie serie=new Serie(nameS.getText().toString(),
                company.getText().toString(),
                year.getText().toString(),
                genre.getText().toString());

        database.child("series").child(nameS.getText().toString()).setValue(serie).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Main3Activity.this,"Datos insertados correctamente",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Main3Activity.this,"No hubo inserción",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void eliminar(){
        final EditText id = new EditText(this);
        id.setHint("Juego a eliminar");
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Buscar Id").setMessage("Nombre del juego:").setView(id).setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminar(id.getText().toString());
            }
        }).setNegativeButton("Cancelar", null).show();
    }

    private void eliminar(String n){
        database.child("series").child(n).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Main3Activity.this,"Eliminación correcta",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Main3Activity.this,"No se pudo eliminar",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consultar(){
        final EditText id = new EditText(this);
        id.setHint("Nombre");
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Busqueda").setMessage("Nombre de la serie a buscar:").setView(id).setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mostrar(id.getText().toString());
            }
        }).setNegativeButton("Cancelar", null).show();
    }

    private void mostrar(String i){
        database.child("series").child(i)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Serie serie = dataSnapshot.getValue(Serie.class);

                        if(serie!=null) {
                            nameS.setText(serie.nombre);
                            company.setText(serie.compania);
                            year.setText(serie.anio);
                            genre.setText(serie.genero);
                        } else {
                            Toast.makeText(Main3Activity.this,"No se puede realizar la busqueda",Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void actualizar(){
        if(nameS.getText().toString().equals("") ||
                company.getText().toString().equals("") ||
                year.getText().toString().equals("") ||
                genre.getText().toString().equals("")){
            Toast.makeText(Main3Activity.this,"No puede dejar campos vacios",Toast.LENGTH_SHORT).show();
            return;
        }

        Serie serie=new Serie(nameS.getText().toString(),
                company.getText().toString(),
                year.getText().toString(),
                genre.getText().toString());

        database.child("series").child(nameS.getText().toString()).setValue(serie);

    }
}
