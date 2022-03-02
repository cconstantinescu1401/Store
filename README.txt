Constantinescu Ciprian, 321CB

In cadrul implementarii, am folosit biblioteca externa Apache Commons CSV pentru
a citi datele din fisierul csv si pentru a crea un alt fisier csv.
De asemenea, am folosit ArrayList-uri in loc de vectori.

Pentru implementarea programului cu ajutorul caruia sa fie gestionate stocurile
unui magazin, am creat clasele urmatoare, fiecare avand un rol specific:

- Store
	-Clasa care reprezinta magazinul, la care am aplicat design pattern-ul
	Singleton. Astfel, aceasta contine campul instance, in care este retinuta
	unica instanta, si metoda getInstance.
	-In cadrul clasei am adaugat un constructor fara parametri, getteri si
	setteri, si majoritatea metodelor necesare pentru executarea comenzilor:
		-readCSV: metoda in care, cu ajutorul unui obiect CSVParser din
			biblioteca externa Apache Commons CSV, am citit datele
			din fisierul csv mentionat, creand astfel store-ul
			corespunzator. Am tratat exceptiile ce ar putea aparea
			in urma apelarii altor functii ajutatoare.
		-saveCSV: metoda care scrie datele despre produsele din store
			in fisierul CSV, folosind un CSVPrinter din biblioteca
			externa
		-addProduct: metoda care adauga produsul primit ca parametru in
			ArrayList-ul ce contine produsele, sau arunca exceptia
			DuplicateProductException, daca mai exista deja un produs
			cu acelasi uniqueId
		-productExistsInArray: metoda care verifica daca in ArrayList-ul
			de produse exista un produs cu uniqueId-ul mentionat
		-addManufacturer: metoda care adauga un producator primit ca 
			parametru in ArrayList-ul ce contine producatori, sau arunca
			exceptia DuplicateManufacturerException, daca mai exista deja un
			producator cu acelasi nume
		-findManufacturer: metoda care cauta in lista de producatori un
			producator cu numele specificat, si returneaza acel producator,
			daca a fost gasit, sau null, daca nu a fost gasit
		-createCurrency: metoda care adauga un currency in ArrayList-ul de
			currency-uri, sau arunca exceptia DuplicateCurrencyException,
			daca currency-ul deja exista in ArrayList
		-changeCurrency: metoda care schimba currency-ul curent, modificand
			preturile produselor in functie de parity, sau arunca exceptia
			CurrencyNotFoundException, daca currency-ul dat ca parametru
			nu a fost gasit.
		-findCurrencyBySymbol: metoda care cauta in ArrayList-ul de currencies
			currency-ul cu simbolul specificat, si returneaza acel currency,
			daca a fost gasit, sau null, daca nu a fost gasit
		-findCurrencyByName: metoda care cauta in ArrayList-ul de currencies
			currency-ul cu numele specificat, si returneaza acel currency,
			daca a fost gasit, sau null, daca nu a fost gasit
		-createDiscount: metoda care adauga un discount in ArrayList-ul de
			discount-uri
		-applyDiscount: metoda care aplica discount-ul specificat tuturor
			produselor din store, si arunca exceptia DiscountNotFoundException,
			daca discount-ul nu a fost gasit, sau exceptia NegativePriceException,
			daca pretul unui produs dupa aplicarea discount-ului devine negativ
		-getProductsByManufacturer: metoda care returneaza un ArrayList ce contine
			toate produsele producatorului specificat
		-calculateTotal: metoda care primeste o lista de produse si returneaza
			suma preturilor acelor produse
		-findProductById: metoda care cauta in lista de produse produsul cu id-ul
			primit ca parametru, urmand sa returneze acel produs, daca a fost
			gasit, sau null, daca nu a fost gasit
		-findDiscount: metoda care cauta in lista de discounturi discountul cu
			tipul si valoarea primite ca parametru, urmand sa returneze acel
			produs, daca a fost gasit, sau null, daca nu a fost gasit
		-convertPrice: functie care primeste un String ce reprezinta un pret si
			returneaza pretul drept o valoare double. Daca String-ul contine
			un symbol al unui currency care nu apare in lista de currencies,
			metoda va arunca exceptia CurrencyNotFound
		-listProducts: metoda care afiseaza, folosind PrintWriter-ul primit ca
			parametru, informatii despre toate produsele din store
		-listCurrencies: metoda care afiseaza, folosind PrintWriter-ul primit ca
			parametru, informatii despre toate currency-urile din store
		-listDiscounts: metoda care afiseaza, folosind PrintWriter-ul primit ca
			parametru, informatii despre toate discount-urile din store
		-listManufacturers: metoda care afiseaza, folosind PrintWriter-ul primit
			ca parametru, informatii despre toti producatorii din store
		-saveStore: metoda BONUS care salveaza store-ul intr-un fisier binar cu
			toate setarile sale. Am folosit un ObjectOutputStream pentru a
			salva obiectul in fisierul binar cu numele specificat, astfel ca
			am decis ca Store sa implementeze Serializable, urmand apoi ca si
			clasele Product, Manufacturer, Currency si Discount sa
			implementeze Serializable.
		-loadStore: metoda BONUS care incarca store-ul dintr-un fisier binar cu
			toate setarile sale. Am folosit un ObjectInputStream pentru a
			incarca obiectul in fisierul binar cu numele specificat, astfel
			ca am decis ca Store sa implementeze Serializable, urmand apoi ca
			si clasele Product, Manufacturer, Currency si Discount sa
			implementeze Serializable.

-Currency
	-Clasa care reprezinta un currency.
	-In cadrul clasei am implementat un constructor cu parametri, unul fara parametri,
	getteri pentru variabilele membru, metoda updateParity(care actualizeaza campul
	parityToEur), si metoda toString(care returneaza un String ce contine name si
	parityToEur).

-Discount
	-Clasa care reprezinta un discount.
	-In cadrul clasei am implementat un constructor cu parametri, unul fara parametri,
	niste getteri si setteri, metoda setAsAppliedNow(care actualizeaza campul
	lastDateApplied), si metoda toString(care returneaza un String ce contine toate
	informatiile depsre discount).

-Manufacturer
	-Clasa care reprezinta un producator.
	-In cadrul clasei am implementat un constructor cu parametri, unul fara parametri,
	niste getteri si setteri, metoda addNewProduct(care incrementeaza 1 in 
	countProducts), si metoda toString(care returneaza un String ce contine numele
	producatorului).

-Product
	-Clasa care reprezinta un produs.
	-In cadrul clasei am implementat un constructor fara parametri, niste
	getteri si setteri, metoda addNewProduct(care incrementeaza 1 in 
	countProducts), si metoda toString(care returneaza un String ce contine toate
	informatiile despre produs).

-ProductBuilder
	-Clasa folosita pentru a aplica design pattern-ul Builder asupra Product.
	-In cadrul clasei, am implementat metoda care seteaza campurile produsului si
	metoda build, care returneaza produsul.

In metoda main din clasa Main, vor fi citite numele fisierului input(care contine
comenzile ce vor fi executate) si numele fisierului output(in care va fi printat
output-ul corespunzator). Am folosit un Scanner pentru a citi linie cu linie din
fisierul cu comenzi, urmand apoi a compara primul cuvant din acea linie, pentru
a determina ce comanda va fi executata. De asemenea, am tratat exceptiile ce pot
aparea dupa executarea anumitor comenzi. Am folosit un PrintWriter pentru afisarea
outputului, iar, in cazul unor exceptii, un anumit mesaj va fi printat in
System.err .

Pentru testare am folosit fisierul amazon_co-ecommerce_sample.csv . Am incarcat
in arhiva si fisierele cu comenzi si outputul aferent.