# Exercise5
İzinler,Dosyaya Gitme,Resim Seçme,SQL'e kaydetme-geri çağırma

Menu EKLEMEK
OptionMenu yarat,\n
	MenuInflater inflater =getMenuInflater();\n
   inflater.inflate(R.menu.menu,menu);

OptionMenu ‘de bir item seçilirse ,
	if(item.getItemId() == R.id.menuID){...}


					İZİNLER
İzin Kontrolü Yap,
	VAR ise; SEÇ(action)
	YOK ise; İZİN ister
İzin isteme Sonucu,
	POZİTİF   ise; SEÇ(action)
	NEGATIF ise; İZİN iste
SEÇ işlemi Sonuçları,
	Seçilen DATA bilgilerini AL


