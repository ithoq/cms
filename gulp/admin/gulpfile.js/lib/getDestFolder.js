var config = require('../config')

module.exports = function(env) {
  return (process.env.NODE_ENV === 'production') ? config.root.destProd : config.root.dest;
}
