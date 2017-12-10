const express = require('express')
const proxy = require('express-http-proxy');

// Create express router
const router = express.Router()

// Transform req & res to have the same API as express
// So we can use res.status() & res.json()
var app = express()

router.use((req, res, next) => {
  Object.setPrototypeOf(req, app.request)
  Object.setPrototypeOf(res, app.response)
  req.res = res
  res.req = req
  next()
})

router.use('/api', proxy('localhost:8080', {
  proxyReqPathResolver: function(req) {
    return "/api" + require('url').parse(req.url).path;
  },
  proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
    // you can update headers
    console.log(srcReq.session.token.value)
    proxyReqOpts.headers['Grpc-Metadata-Authorization'] = "Bearer "+ srcReq.session.token.value;
    return proxyReqOpts;
  }
}));

// Add POST - /api/login
router.post('/login', (req, res) => {
  if (req.body.username === 'demo' && req.body.password === 'demo') {
  req.session.token = { value: "<dummy token>" }
    return res.json({ username: 'demo' })
  }
  res.status(401).json({ message: 'Bad credentials' })
})

// Add POST - /api/logout
router.post('/logout', (req, res) => {
  delete req.session.authUser
  res.json({ ok: true })
})

// Export the server middleware
module.exports = {
  path: '/',
  handler: router
}
