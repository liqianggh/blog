/**
 * @see https://vuepress.vuejs.org/zh/
 */
module.exports = {
  port: '4000',
  dest: 'dist',
  base: '/',
  title: 'Java填坑笔记',
  description: 'Java 填坑记录',
  head: [['link', { rel: 'icon', href: `/favicon.ico` }]],
  markdown: {
    code: {
      lineNumbers: true,
    },
    externalLinks: {
      target: '_blank',
      rel: 'noopener noreferrer',
    },
  },
  themeConfig: {
    logo: 'https://source.mycookies.cn/3f4fb78ab4aec2948d6c40584c235b9e.jpeg',
    repo: 'liqianggh/blog',
    repoLabel: 'Github',
    docsDir: 'docs',
    docsBranch: 'master',
    editLinks: true,
    smoothScroll: true,
    locales: {
      '/': {
        label: '简体中文',
        selectText: 'Languages',
        editLinkText: '帮助我们改善此页面！',
        lastUpdated: '上次更新',
        nav: [
          {
            text: 'MySQL',
            link: '/mysql/',
          },
          {
            text: 'Test',
            link: '/test/',
          },
        ],
        sidebar: 'auto',
        sidebarDepth: 2,
      },
    },
  },
  plugins: [
    [
      '@vuepress/container',
    ],
    [
      '@vuepress/active-header-links',
      {
        sidebarLinkSelector: '.sidebar-link',
        headerAnchorSelector: '.header-anchor',
      },
    ],
    [
      '@vuepress/pwa',
      {
        serviceWorker: true,
        updatePopup: true,
      },
    ],
    [
      '@vuepress/last-updated',
      {
        transformer: (timestamp, lang) => {
          // 不要忘了安装 moment
          const moment = require('moment')
          moment.locale(lang)
          return moment(timestamp).fromNow()
        },
      },
    ],
    ['@vuepress/medium-zoom', true],
    [
      'container',
      {
        type: 'vue',
        before: '<pre class="vue-container"><code>',
        after: '</code></pre>',
      },
    ],
    [
      'container',
      {
        type: 'upgrade',
        before: (info) => `<UpgradePath title="${info}">`,
        after: '</UpgradePath>',
      },
    ],
    ['flowchart'],
    [
      'vuepress-plugin-mygitalk',{
        // 是否启用(关闭请设置为false)(default: true)
        enable: true,
        // 是否开启首页评论(default: true)
        home: true,
        // Gitalk配置
        gitalk: {
          // GitHub Application Client ID.
          clientID: '556b3313997ea5ab29a0',
          // GitHub Application Client Secret.
          clientSecret: '49271bf6819717c6c1bd10fb83190d2c0442d9d6',
          // GitHub repository. 存储评论的 repo
          repo: 'blog',
          // GitHub repository 所有者，可以是个人或者组织。
          owner: 'liqianggh',
          // GitHub repository 的所有者和合作者 (对这个 repository 有写权限的用户)。(不配置默认是owner配置)
          admin: ['liqianggh'],
          // 设置语言(default: zh-CN)
          language: 'zh-CN',
        }
      }
    ],
  ],
}
