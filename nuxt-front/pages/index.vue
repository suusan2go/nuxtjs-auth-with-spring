<template>
  <section class="container">
    <div>
      <logo/>
      <h1 class="title">
        nuxt-front
      </h1>
      <h2 class="subtitle">
        Nuxt.js project
      </h2>
      <div class="links">
        <a href="https://nuxtjs.org/" target="_blank" class="button--green">Documentation</a>
        <a href="https://github.com/nuxt/nuxt.js" target="_blank" class="button--grey">GitHub</a>
      </div>
        <button @click="hello()">gRPC</button>
    </div>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import Logo from '~/components/Logo.vue'
import { GreeterApi } from '~/api/greeter'

const greeterApi = new GreeterApi({ basePath: "http://localhost:8080"})

export default Vue.extend({
  components: {
    Logo
  },
  methods: {
    hello: async function() {
      const message = await greeterApi.hello({ name: "Kenta" })
      alert(message.greeting)
    }
  }
})
</script>

<style>
.container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.title {
  font-family: "Quicksand", "Source Sans Pro", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif; /* 1 */
  display: block;
  font-weight: 300;
  font-size: 100px;
  color: #35495e;
  letter-spacing: 1px;
}

.subtitle {
  font-weight: 300;
  font-size: 42px;
  color: #526488;
  word-spacing: 5px;
  padding-bottom: 15px;
}

.links {
  padding-top: 15px;
}
</style>
