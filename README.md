<h1 align="center">
  React Native Springboard Shortcuts
</h1>

React Native Springboard Shortcuts provides an easy way to create shortcuts to an application when developing in React Native.

The shortcuts can deeplink to a specific screen(s) inside your app. This library take advantage of the native side with a simple API for the JavaScript side. At the moment the library only supports shortcuts for Android devices, in the future we may add support for iOS as well.
<br/>
<br/>

## Requirements
- Android SDK version >= `25`

## Getting started
`$ npm install --save react-native-springboard-shortcuts`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.alon.ReactNativeSpringboardShortcuts.RNSpringboardShortcutsPackage;` to imports at the top of the file
  - Add `new RNSpringboardShortcutsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	 include ':react-native-springboard-shortcuts'
     project(':react-native-springboard-shortcuts').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-springboard-shortcuts/lib/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
     implementation project(":react-native-springboard-shortcuts")
  	```

<br/>

## Example

```javascript
import RNSpringboardShortcuts from 'react-native-springboard-shortcuts';

RNSpringboardShortcuts.isShortcutServiceAvailable((isAvailable: boolean) => {

  if (isAvailable) {
    const shortcut = {
      id: 'shortcut.id.example',
      shortLabel: 'Shortcut title',
      longLabel: 'Shortcut description',
      imageUrl: 'https://someImage.jpg',
      defaultImageUrl: 'https://someDefaultImage.jpg',
      intentUri: `myApp://myCoolAppScreen`
    };

    RNSpringboardShortcuts.exists(shortcut.id).then(() => {
      // Exists
      RNSpringboardShortcuts.updateShortcut(shortcut);
    }).catch((_err: Error) => {
      // Does not exist
      RNSpringboardShortcuts.addShortcut(shortcut);
    });
   }
});

```

<br/>

## API

#### isShortcutServiceAvailable
```javascript
RNSpringboardShortcuts.isShortcutServiceAvailable((isAvailable: boolean) => {

});
```

#### exists
```javascript
RNSpringboardShortcuts.exists(shortcut.id).then(() => {
  // Exists
}).catch((_err: Error) => {
  // Does not exist
});
```

#### addShortcut
```javascript
RNSpringboardShortcuts.addShortcut(shortcut).then(() => {
  // Succeed
}).catch((_err: Error) => {
  // Error
});
```

#### updateShortcut
```javascript
RNSpringboardShortcuts.updateShortcut(shortcut).then(() => {
  // Succeed
}).catch((_err: Error) => {
  // Error
});
```

#### removeShortcut
```javascript
RNSpringboardShortcuts.removeShortcut(shortcut.id);
```

#### removeAllShortcuts
```javascript
RNSpringboardShortcuts.removeAllShortcuts();
```

#### handleShortcut
This function will only work if you **did NOT** provide an `intentUri` to the shortcut.
To use this method for handling shortcuts, copy it to `componentDidMount` function of a component/screen which loads at the start of your app.
```javascript
RNSpringboardShortcuts.handleShortcut((shortcutId: string) => {

});
```

<br/>
So what are you waiting for? Now that you know how to use it, start implementing React Native Springboard Shortcuts in your project today!
