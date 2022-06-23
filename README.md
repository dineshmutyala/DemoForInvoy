# DemoForInvoy

## App Archtecture
    - Single Activity + multi-Fragment
    - MVVM + Repository model
    - Uses Android Navigation (graph)
    - Dagger for DI
    - ViewBinding
    - Firebase (for Authentication, FireStore Database for chat, Firebase Storage for graph images, Cloud Messaging for realtime chat)
\
&nbsp;
- App Features
    - The Intro screen, checks for an existing logged in user and logs them back in automatically(Only for Google Sign In)
    - The App works in two modes
    - User Mode
        - You can sign in through Google and you would be in User Mode.
    - Coach Mode
        - There's already a dummy coach user created, through Password Autentication. Sign in through Email on the Intro screen -> Email, Password pre-populated; Sign In and you would be in Coach Mode

- User Mode
    - On the Home screen,
        - top-right overflow button
            - Generate Test weights data -> Generate test data for past 60 days
            - Clear all weights data -> Clears all the weights data
            - Sign out -> Sign the User out
        - Button for you to enter today's weight if not already; if already entered today's weight, it would be shown here - Tapping on it you can update your today's weight.
        - Weight graph - Using MPAndroidChart for rendering chart.
            - The chart displays data(of past 7 days) as it is updated; data = line chart + values and difference from previous weight.
            - <i>There are some rendering issues in the chart, which I did not feel worth spending time on. Felt MPAndroidChart is great but not powerful enough. My first time working with charts and I might not've done a great job. :cold_sweat: May be a custom implementation would've been much easier :smiling_imp:</i>
        - A button on the top-right of the Chart view - to expand the chart and view all the history, in a paginated manner.
        - Send Graph to coach button -> to send the graph image to the coach
        - Chat with Coach FAB -> to Chat with Coach
\
&nbsp;
    - Input weight screen (Pretty straight forward)
        - Text field for you to enter you weight for today
        - update button
        - All data is saved to Room db locally
\
&nbsp;

    - Weight Graph Screen
        - Displays data in either graph or as List
            - Wanted to add edit to any historical data, but couldn't(other important things to do! Right?)
            - You can navigate back to the home screen from here.
\
&nbsp;
    - Send Graph to coach
        - Snapshots the above graphview and sends  it to the coach. Then navigates to the Chat for you as well :sunglasses:
\
&nbsp;
    - Chat Screen
        - Is fixed to only chat with our one and only Coach.
        - White bubbles -> Messages from coach
        - Green bubbles -> Messages you send
          - As soon as you hit send, the new message would be added to the chat and ti would be shown faded => Not synced with server yet.
          - Once the message is inserted in the db in Firebase, it would look like the other messages you sent.
        - When in chat and your Coach send you a message, it would be added to the chat(through FCM)
        - You can navigate back to the HomeScreen from here
\
&nbsp;
    - Sign Out
      - Signs the current user out and brings you back to the Intro screen
\
&nbsp;
- Coach Mode
  - <i><b>When logged in through pre-populated email and password, you can see the Coach mode</i></b>
  - Conversaton Screen
    - Coach can see all the conversations and tap into each of the chats
    - Everything else is the same as a regular chat, except for the Realtime updates of messages the clients send to Coach. (Not implemented)

\
&nbsp;
- Known Issues/ Out of scope/ Not implemented
    - The Graph x-Axis values don't match the number of points shown on the graph
    - Coach chat doesn't have real-time updates(Out of scope due to not being able to handle multiple login providers)
    - No notification is shown when a message is received and app is in background (Not implemented)
        - Expected the SDK to handle notification when app in bakground, but looks like it doesn't or may be need some extra setup.
    - App Offline support not available for Chat/ Login auth. (Not Implemented - Time constraint)
    - App doesn't sync with Firebase for weights data.(Not Implemented - Time constraint)
    - App design specifically in portrait mode. Landscape might mess up the UI.(Not Implemented - :neutral_face:)
