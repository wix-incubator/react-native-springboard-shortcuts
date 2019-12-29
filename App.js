/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {NativeModules, StyleSheet, Text, View, Image, Button, Platform} from 'react-native';
let RNSpringboardShortcuts = NativeModules.RNSpringboardShortcuts;

type Props = {};

export default class App extends Component<Props> {

  constructor(props: Props) {
    super(props);
  }

  componentDidMount() {
    RNSpringboardShortcuts.handleShortcut(function(id) {
      // Do anything you want. Just like navigate to specify page by the id and so on.
      console.log(`Shortcut with id ${id} was opened`);
    });
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.description}>Testing Springboard Shortcuts library</Text>
        <Button
          onPress={this.createShortcut}
          title="Create a shortcut"
          color="#841584"
        />
      </View>
    );
  }

  createShortcut = async () => {
    console.log(`Createing shortcut`);
    if (Platform.OS === "android") {

      let shortcut = {
        id: "someId_1",
        shortLabel: "Cool place",
        longLabel: "What a cool place this is!",
        imageUrl: "https://www.smartbonny.com/wp-content/uploads/2019/05/551103-1TOqFD1502285018-540x405.jpg"
      };
      // RNSpringboardShortcuts.removeAllShortcuts();

      RNSpringboardShortcuts.exists(shortcut.id).then(() => {
        // Exists
        RNSpringboardShortcuts.updateShortcut(shortcut);
      }).catch((err) => {
        // Not exists
        RNSpringboardShortcuts.addShortcut(shortcut);
      });
    }
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF',
    marginHorizontal: 16,
    marginTop: 60,
  },
  description: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
});
