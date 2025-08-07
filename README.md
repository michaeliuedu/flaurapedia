# Flaurapedia - Android Firebase and TfLite Application
**Description:** An android application for plant identification and blogging

**Notice:**
This app is not on GitHub for distribution but rather for references and storage. Some lines were altered for anonimity and prevent database credential leaks. 

***Application renamed from Vanguard to Flauratic to Flaurapedia in final revision which may be why some files have incorrect names. The project should still work as expected.***


***Firebase authentication is required within Android Studio: Tools -> Firebase -> Should be prompted to connect console. Firebase console Realtime Database -> Rules:***

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

**Congressoinal App Challenge Link**
https://www.congressionalappchallenge.us/21-tx24/
