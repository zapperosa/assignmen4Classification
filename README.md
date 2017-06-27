# Pràctica 4: Classificació

Aquesta aplicació us proporciona un entorn de conducció on heu de conduir evitant de xocar amb els cotxes que aneu avançant. Cal també que procureu conduïr pels carrils més centrals possible.
Es tracta d'anar conduint per generar "data sets" que posteriorment usarem per a fer classificació. Concretament heu de conduir durant 4 minuts procurant anar pel carril central el màxim de temps possible. El programa uns generarà un fitxer de dades cada 30 segons. Aquests fitxers els trobareu a l'arrel c:\CCompensat\TiempoXX.arff on les XX indiquen els segons de conducció (30, 60, 90, ..., 240). Són fitxers que estan en el format del WEKA. En les vostres proves només haureu de fer servir els fitxers que corresponen als segons següents: 30, 60, 120, 180, 240.

## Els fitxers de dades
Els fitxers de dades contenen els següents atributs
* Carril: el carril pel que circula el cotxe. Pot ser: CARRILARCENIZQ,CARRILIZQ,CARRIALMED,CARRIALDER,CARRILARCENDER
* Class: l'acció que es realitza: Recte,Esquerra,Dreta
* di, dm, dd: distància dels cotxes que s'aproximen per cada carril: di - esquerra, dm - mig, dd: - dret

## Classificació - WEKA

El Weka http://www.cs.waikato.ac.nz/ml/weka/ és un programa i/o llibreria que implementa un munt d'algorismes de "machine learning". Està fet a la universistat de Waikako a Nova Zelanda. 

Un cop generats els "datasets" haureu d'usar el Weka per classificar-los i veure quina taxa d'encerts podem obtenir amb els diferents trainig sets i els algorismes de classificació:
* trees/J48: és una millora de l'algorisme de l'arbre de decisió vist a classe (ID3)
* lazy/IBk: és un K-NearestNeighbour. Podeu especificar la k que voleu usar
* bayes/naiveBayes 

## Ús del Weka
Per classificar les dades haureu de seguir els passos següents
1. Obriu el Weka i us apareix una finestra petita. Premeu el botó que diu **Explorer**. S'obra una nova finestra
2. Escolliu la pestanya que diu **Preprocess**. Premeu el botó de dalt a la dreta que diu **Open file** i escolliu el fitxer de dades que desitgeu. Podeu exporar una mica les dades per veure com són.
3. Escolliu la pestanya **Classify**
  1. A *classifier* premeu la pestanya *Choose* per escollir el classificador desitjat (veieu la llista del punt anterior)
  2. Assegureu-vos que a *Test options* hi ha escollit *Cross validation. Folds 10*
  3. Assegureu-vos que al selctor de sota hi ha escollit l'atribut *class*. Aquest és l'atribut que s'usa com a classe
4. Podeu prémer el boto **Start** que farà la classificació
5. A **classifier output** veureu les estadístiques dels resultats. En concret ens interessarem per
  * Correctly classified instances
  * True Positive Rate (TP Rate)
  * False Positive Rate (FP Rate)
  * Confusion matrix
  
## Informe a lliurar
En aquesta pràctica no heu de codificar res :-| "només" haureu de fer un informe amb els resultats obtinguts. Concretament us demano:
Executar cada algorisme de classificació (J48, IBk, K-NaiveBayes) amb els datasets de més petit a més gran (els que corresponen als segons de conducció: 30, 60, 120, 180, 240). Per cada execució anoteu en una taula les estadístiques esmentades anteriorment. Els confusion matrix no els poseu a la taula. A més en el cas del K-NN heu de trobar la millor k (que possiblement serà 1, 3 o 5)

Haureu de escriure un apartat comentant els resultats obtinguts. Com a mínim:
* Com evoluciona els resutats a mesura que usem datasets més grans. Quina és la grandària a partir de la qual ja no millora
* Compareu els resultats obtinguts en els tres algorismes de classificació diferents
* En el cas de l'arbre de desició, li veieu alguna lògica que pugeu explicar
* En el cas del K-NN, és la mateixa K sempre la que dóna el millor resultat per tots els tamanys dels datasets

## Informe Practica 4 Clasification Arnau Casanova Barjuan

##Algorisme J48

![j48](https://user-images.githubusercontent.com/9919396/27582286-a89dbd00-5b30-11e7-859b-0dcfe8744435.png)


* Com evoluciona els resutats a mesura que usem datasets més grans. Quina és la grandària a partir de la qual ja no millora

* Compareu els resultats obtinguts en els tres algorismes de classificació diferents

* En el cas de l'arbre de desició, li veieu alguna lògica que pugeu explicar

* En el cas del K-NN, és la mateixa K sempre la que dóna el millor resultat per tots els tamanys dels datasets

##Algorisme IBk

* Com evoluciona els resutats a mesura que usem datasets més grans. Quina és la grandària a partir de la qual ja no millora

* Compareu els resultats obtinguts en els tres algorismes de classificació diferents

* En el cas de l'arbre de desició, li veieu alguna lògica que pugeu explicar

* En el cas del K-NN, és la mateixa K sempre la que dóna el millor resultat per tots els tamanys dels datasets

##Algorisme K-Naive-Bayes

* Com evoluciona els resutats a mesura que usem datasets més grans. Quina és la grandària a partir de la qual ja no millora

* Compareu els resultats obtinguts en els tres algorismes de classificació diferents

* En el cas de l'arbre de desició, li veieu alguna lògica que pugeu explicar

* En el cas del K-NN, és la mateixa K sempre la que dóna el millor resultat per tots els tamanys dels datasets
