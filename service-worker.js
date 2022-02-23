/**
 * Welcome to your Workbox-powered service worker!
 *
 * You'll need to register this file in your web app and you should
 * disable HTTP caching for this file too.
 * See https://goo.gl/nhQhGp
 *
 * The rest of the code is auto-generated. Please don't update this file
 * directly; instead, make changes to your Workbox build configuration
 * and re-run your build process.
 * See https://goo.gl/2aRDsh
 */

importScripts("https://storage.googleapis.com/workbox-cdn/releases/4.3.1/workbox-sw.js");

self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});

/**
 * The workboxSW.precacheAndRoute() method efficiently caches and responds to
 * requests for URLs in the manifest.
 * See https://goo.gl/S9QRab
 */
self.__precacheManifest = [
  {
    "url": "404.html",
    "revision": "a71a45420845f812c648fc55079e7fc6"
  },
  {
    "url": "assets/css/0.styles.305e47ad.css",
    "revision": "e86d890c5806c36f24f66b38c13e0d52"
  },
  {
    "url": "assets/img/search.83621669.svg",
    "revision": "83621669651b9a3d4bf64d1a670ad856"
  },
  {
    "url": "assets/js/10.d7ec4308.js",
    "revision": "314b0462f3444a6c4b36b68b8ee6aa0b"
  },
  {
    "url": "assets/js/11.b5f23ef6.js",
    "revision": "65156f6501312139c26c6ca50fc4f4ba"
  },
  {
    "url": "assets/js/12.9453a994.js",
    "revision": "28c3e57a50414e35855df12784ffdd3a"
  },
  {
    "url": "assets/js/13.6e6326e5.js",
    "revision": "484ea13a969601099930add6c1b7d741"
  },
  {
    "url": "assets/js/14.8bfd546f.js",
    "revision": "c50ccd86bf9753e9e405d3870a8a6f90"
  },
  {
    "url": "assets/js/15.a4a5fcd3.js",
    "revision": "96a3d0d62cf9107c1d66973e58c69d32"
  },
  {
    "url": "assets/js/16.08be59e0.js",
    "revision": "d82f1c1f40e26ecde1c5647eb3df9493"
  },
  {
    "url": "assets/js/17.9dda6c34.js",
    "revision": "69a03571b252bf8ab05d3d9ecc2912f2"
  },
  {
    "url": "assets/js/18.690c3b19.js",
    "revision": "2b7e4d9a1f37a54dca343d91e9fbc8e9"
  },
  {
    "url": "assets/js/19.aebd94b0.js",
    "revision": "843990b71b482efb944532fa0f3df805"
  },
  {
    "url": "assets/js/4.d59f4fc8.js",
    "revision": "1422861e8b1862ebb348a69c45e2e86a"
  },
  {
    "url": "assets/js/5.c57c3a10.js",
    "revision": "f1ddf45fc1905fe90c78a903e6747aa5"
  },
  {
    "url": "assets/js/6.40650c05.js",
    "revision": "b42f5cc2e19bfdba53714cbc52e8102c"
  },
  {
    "url": "assets/js/7.afd745b9.js",
    "revision": "748214941e8c74c14520fd710d27780c"
  },
  {
    "url": "assets/js/8.a0081c7e.js",
    "revision": "434e440991dab1c915605af2d4c5eccb"
  },
  {
    "url": "assets/js/9.2e5f72b7.js",
    "revision": "d1badb7fe1a8ba916d6e95fb42b8016f"
  },
  {
    "url": "assets/js/app.d6956536.js",
    "revision": "e7d02010ba75767818c7630b7c90a14b"
  },
  {
    "url": "assets/js/vendors~flowchart.292f02c3.js",
    "revision": "c15614723b75f5e0dca5b99264255fd0"
  },
  {
    "url": "assets/js/vendors~notification.4fd649ec.js",
    "revision": "8e0b66833ae16638de1bbe805fea7460"
  },
  {
    "url": "images/javaTKBJ-1.jpeg",
    "revision": "fbead4ad1ef7e7232d78fd94b52c8313"
  },
  {
    "url": "index.html",
    "revision": "99c4cfda5cff6c9a11decaf43737ce99"
  },
  {
    "url": "mysql-index-1.html",
    "revision": "dbc6f2508882f25d012ae7d1d424d1e1"
  },
  {
    "url": "mysql/index.html",
    "revision": "d8ba714a29e8a31af97ccd150008df7c"
  },
  {
    "url": "mysql/mysql-index-1.html",
    "revision": "286445b27cf39610ad737ad0653a696e"
  },
  {
    "url": "mysql/mysql-index-2.html",
    "revision": "e79caa618402154435b1d271d5aad679"
  },
  {
    "url": "mysql/mysql-index-3.html",
    "revision": "b585d654348168f7d847fb4e40aaa7e4"
  },
  {
    "url": "test/experiment-md.html",
    "revision": "5eb28782133b88fe9dcd5fa43ea8a8ae"
  },
  {
    "url": "test/index.html",
    "revision": "b76e6908f362b03106db35ecf2193810"
  },
  {
    "url": "test/jmh-tutorial.html",
    "revision": "f9e41447a87009c6510cb80f57b3ce01"
  }
].concat(self.__precacheManifest || []);
workbox.precaching.precacheAndRoute(self.__precacheManifest, {});
addEventListener('message', event => {
  const replyPort = event.ports[0]
  const message = event.data
  if (replyPort && message && message.type === 'skip-waiting') {
    event.waitUntil(
      self.skipWaiting().then(
        () => replyPort.postMessage({ error: null }),
        error => replyPort.postMessage({ error })
      )
    )
  }
})
