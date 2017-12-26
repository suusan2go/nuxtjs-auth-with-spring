const express = require('express');
const proxy = require('express-http-proxy');

// Create express router
const router = express.Router();

// Transform req & res to have the same API as express
// So we can use res.status() & res.json()
const app = express();

router.use((req, res, next) => {
  Object.setPrototypeOf(req, app.request);
  Object.setPrototypeOf(res, app.response);
  req.res = res;
  res.req = req;
  next();
});

router.use(
  '/api',
  proxy('localhost:8080', {
    proxyReqPathResolver(req) {
      return '/api' + require('url').parse(req.url).path;
    },
    proxyReqOptDecorator(proxyReqOpts, srcReq) {
      // you can update headers
      // console.log(srcReq.session.token.value)
      proxyReqOpts.headers['Grpc-Metadata-Authorization'] = 'Bearer ' + 'dummry';
      return proxyReqOpts;
    },
  }),
);

// Add POST - /api/login
router.post('/login', (req, res) => {
  if (req.body.username === 'demo' && req.body.password === 'demo') {
    req.session.token = { value: '' };
    return res.json({ username: 'demo' });
  }
  res.status(401).json({ message: 'Bad credentials' });
});

// Add POST - /api/logout
router.post('/logout', (req, res) => {
  delete req.session.authUser;
  res.json({ ok: true });
});

// Export the server middleware
module.exports = {
  path: '/',
  handler: router,
};
