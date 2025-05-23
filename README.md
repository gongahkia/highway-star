[![](https://img.shields.io/badge/highway_star_1.0.0-passing-green)](https://github.com/gongahkia/highway-star/releases/tag/1.0.0) 

# `Highway Star`

*"We have [Strava](https://www.strava.com/) at home"*, featuring IP-based geolocation, activity recording and basic account management.

Made to refamiliarise myself with [Java and friends](#stack).

## Stack

* *Frontend*: [Java Swing](https://docs.oracle.com/javase/tutorial/uiswing/index.html)
* *Backend*: [Java](https://www.java.com/en/)
* *DB*: [Firebase Realtime Database](https://firebase.google.com/docs/database)

## Architecture

![](./asset/reference/architecture.png)

## Screenshots

### Login/Registration

<div style="display: flex; justify-content: space-between;">
  <img src="./asset/reference/1.png" width="49%">
  <img src="./asset/reference/2.png" width="49%">
</div>

### Dashboard

<div style="display: flex; justify-content: space-between;">
  <img src="./asset/reference/3.png" width="49%">
  <img src="./asset/reference/4.png" width="49%">
</div>

### Activity History

![](./asset/reference/5.png)

### Profile Management

![](./asset/reference/6.png)

## Usage

First create a [Google Developer Account](https://developers.google.com/).

Then *Create a Firebase project* in [Firebase Console](https://console.firebase.google.com) and navigate to *Project Overview > Project settings > Service accounts > Java*. 

*Generate new private key* and save the downloaded file at the filepath `./highway-star/app/src/main/resources/serviceAccountKey.json`.

Then run the below.

```console
$ git clone https://github.com/gongahkia/highway-star
$ cd highway-star && make
```

## Other notes

[No hate](./asset/logo/none.jpg), but respectfully, [Java Swing]() is [so ugly](https://www.reddit.com/r/javahelp/comments/173nl4d/getting_really_frustrated_with_swing_is_there_a/). I might really have to take [Reddit's advice](https://www.reddit.com/r/JavaFX/comments/18n3sjt/why_javafx_is_still_used_in_2023/) and look into [JavaFX](https://openjfx.io/) instead.

## Reference

The name `Highway Star` is in reference to [Yuya Fungami](https://jojowiki.com/Yuya_Fungami)'s (噴上 裕也) [Stand](https://jojo.fandom.com/wiki/Stand) of the [same name](https://jojowiki.com/Highway_Star) in [Part 4: Diamond is Unbreakable](https://jojowiki.com/Diamond_is_Unbreakable) of the ongoing manga series [JoJo's Bizarre Adventure](https://jojowiki.com/JoJo_Wiki).

<div align="center">
    <img src="./asset/logo/highway_star.png" width="35%">
</div>