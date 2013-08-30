# soundstorm

Simple [ring](https://github.com/ring-clojure) app which uses the [SoundCloud API](http://developers.soundcloud.com/docs/api/reference). Mostly covers the basic boilerplate of a typical clojure webapp. Currently you can login to the app via SoundCloud and view your tracks.

Demo of the app is here: http://ss.ockhamsolutions.de/

_note: there app uses no persistence, so nothing is saved or even cached_

## Configuration

To run the app, you must have a directory `config` in the project root directory. In this directory, there must also be a `dev` and `prod` directory. These directories correspond to the profiles in `project.clj`. The namespace `config` looks for a file named `ss.edn` which is looked up as a classpath resource. The contents of `ss.edn` looks like:

```clj
{:client-config {:client-id "YOUR_SOUNDCLOUD_APP_CLIENT_ID"
                 :client-secret "YOUR_SOUNDCLOUD_APP_CLIENT_SECRET"
                 :callback {:domain "http://localhost.com:3000"
                            :path "/sc-redirect-uri"}}}
```

The above is my development config. I have `localhost.com` set to resolve to localhost on my development machine. This allows you to test the oauth process locally.
