const http = require('http');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const { URL } = require('url');

const PORT = process.env.PORT || 3001;
const JWT_SECRET = process.env.JWT_SECRET || 'trading-journal-secret-key-change-in-prod';
const DATA_DIR = path.join(__dirname, 'data');
const FRONTEND_DIR = path.join(__dirname, '..', 'frontend');
const USERS_FILE = path.join(DATA_DIR, 'users.json');
const JOURNALS_FILE = path.join(DATA_DIR, 'journals.json');

// Ensure data directory
if (!fs.existsSync(DATA_DIR)) {
  fs.mkdirSync(DATA_DIR, { recursive: true });
}
if (!fs.existsSync(USERS_FILE)) fs.writeFileSync(USERS_FILE, '[]');
if (!fs.existsSync(JOURNALS_FILE)) fs.writeFileSync(JOURNALS_FILE, '{}');

// Helpers
function readJSON(file) {
  try {
    return JSON.parse(fs.readFileSync(file, 'utf8'));
  } catch {
    return file.includes('users') ? [] : {};
  }
}

function writeJSON(file, data) {
  fs.writeFileSync(file, JSON.stringify(data, null, 2));
}

function hashPassword(password) {
  return crypto.createHash('sha256').update(password + JWT_SECRET).digest('hex');
}

function createToken(userId) {
  const payload = Buffer.from(JSON.stringify({ userId, exp: Date.now() + 7 * 24 * 60 * 60 * 1000 })).toString('base64');
  const sig = crypto.createHmac('sha256', JWT_SECRET).update(payload).digest('hex');
  return `${payload}.${sig}`;
}

function verifyToken(token) {
  if (!token) return null;
  const [payload, sig] = token.split('.');
  if (!payload || !sig) return null;
  const expected = crypto.createHmac('sha256', JWT_SECRET).update(payload).digest('hex');
  if (sig !== expected) return null;
  try {
    const data = JSON.parse(Buffer.from(payload, 'base64').toString());
    if (data.exp < Date.now()) return null;
    return data.userId;
  } catch {
    return null;
  }
}

function parseBody(req) {
  return new Promise((resolve, reject) => {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', () => {
      try {
        resolve(body ? JSON.parse(body) : {});
      } catch {
        reject(new Error('Invalid JSON'));
      }
    });
  });
}

function send(res, status, data) {
  res.writeHead(status, {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET,POST,PUT,DELETE,OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization'
  });
  res.end(JSON.stringify(data));
}

function getAuthUser(req) {
  const auth = req.headers.authorization || '';
  const token = auth.startsWith('Bearer ') ? auth.slice(7) : null;
  return verifyToken(token);
}

// MIME types for static frontend serving
const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon'
};

function serveStatic(req, res, pathname) {
  let safePath = path.normalize(pathname).replace(/^(\.\.[\/\\])+/, '');
  if (safePath === '/' || safePath === '\\' || !safePath) {
    safePath = 'index.html';
  }

  let filePath = path.join(FRONTEND_DIR, safePath);

  // If path doesn't exist, try index.html (SPA fallback)
  if (!fs.existsSync(filePath) || fs.statSync(filePath).isDirectory()) {
    filePath = path.join(FRONTEND_DIR, 'index.html');
  }

  if (!fs.existsSync(filePath)) {
    res.writeHead(404, { 'Content-Type': 'text/plain' });
    return res.end('404 Not Found');
  }

  const ext = path.extname(filePath).toLowerCase();
  const contentType = MIME_TYPES[ext] || 'application/octet-stream';

  try {
    const content = fs.readFileSync(filePath);
    res.writeHead(200, {
      'Content-Type': contentType,
      'Access-Control-Allow-Origin': '*'
    });
    res.end(content);
  } catch (err) {
    res.writeHead(500, { 'Content-Type': 'text/plain' });
    res.end('500 Internal Server Error');
  }
}

// Seed Demo User and Journal if data is empty
function seedDemoDataIfNeeded() {
  const users = readJSON(USERS_FILE);
  if (!users || users.length === 0) {
    const demoId = 'demo-user-id';
    const demoUser = {
      id: demoId,
      email: 'demo@tradingjournal.com',
      name: 'Demo Trader',
      password: hashPassword('password123'),
      createdAt: new Date().toISOString()
    };
    users.push(demoUser);
    writeJSON(USERS_FILE, users);

    // Seed sample journals for demo user
    const now = new Date();
    const todayStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
    const prevDate = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1);
    const yesterdayStr = `${prevDate.getFullYear()}-${String(prevDate.getMonth() + 1).padStart(2, '0')}-${String(prevDate.getDate()).padStart(2, '0')}`;

    const journals = readJSON(JOURNALS_FILE);
    if (!journals[demoId]) journals[demoId] = {};

    journals[demoId][todayStr] = {
      date: todayStr,
      day: new Date(todayStr + 'T12:00:00').toLocaleDateString('en-US', { weekday: 'long' }),
      startingBalance: 25000,
      closedPL: 277,
      totalBrokerage: 40,
      closingBalance: 25237,
      trades: [
        { name: 'AAPL', buy: '224.50', sell: '228.00', qty: '50', target: '230.00', sl: '222.00', pl: '175.00' },
        { name: 'NVDA', buy: '138.20', sell: '142.50', qty: '40', target: '145.00', sl: '136.00', pl: '172.00' },
        { name: 'TSLA', buy: '255.00', sell: '251.50', qty: '20', target: '260.00', sl: '251.50', pl: '-70.00' }
      ],
      profit: 347,
      loss: 70,
      watchlist: 'MSFT break above 425\nAMD pullback to 155\nSPY 580 call options',
      marketMovements: 'Tech rally led by semiconductors after strong guidance. High morning volatility settling into clean trend.',
      mistakes: 'Exited TSLA near stop-loss without waiting for 15-min candle confirmation, but honored risk limits.',
      riskRewards: 'Maintained 1:2.5 R:R on AAPL and NVDA. Kept max drawdown per trade strictly under 1% of equity.',
      notes: 'Disciplined execution today. Let winners run and cut losers quickly. Prepare watchlist levels tonight.',
      updatedAt: new Date().toISOString()
    };

    journals[demoId][yesterdayStr] = {
      date: yesterdayStr,
      day: new Date(yesterdayStr + 'T12:00:00').toLocaleDateString('en-US', { weekday: 'long' }),
      startingBalance: 24700,
      closedPL: 320,
      totalBrokerage: 20,
      closingBalance: 25000,
      trades: [
        { name: 'MSFT', buy: '420.00', sell: '424.00', qty: '50', target: '426.00', sl: '418.00', pl: '200.00' },
        { name: 'AMZN', buy: '185.50', sell: '188.50', qty: '40', target: '190.00', sl: '183.00', pl: '120.00' }
      ],
      profit: 320,
      loss: 0,
      watchlist: 'AAPL, NVDA, TSLA',
      marketMovements: 'Broad market trended upward with steady breadth.',
      mistakes: 'None major. Stuck firmly to trading rules.',
      riskRewards: 'Targeted 1:2 R:R achieved on both positions.',
      notes: 'Smooth session. Ready for next day.',
      updatedAt: new Date().toISOString()
    };

    writeJSON(JOURNALS_FILE, journals);
  }
}

seedDemoDataIfNeeded();

// Routes
async function handleRequest(req, res) {
  const url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  const method = req.method;
  const pathname = url.pathname;

  // CORS preflight
  if (method === 'OPTIONS') {
    return send(res, 200, {});
  }

  // Public Health check
  if (pathname === '/api/health') {
    return send(res, 200, { status: 'ok', message: 'Trading Journal API is active' });
  }

  try {
    // Auth routes
    if (pathname === '/api/register' && method === 'POST') {
      const body = await parseBody(req);
      const { email, password, name } = body;
      if (!email || !password) return send(res, 400, { error: 'Email and password required' });
      const users = readJSON(USERS_FILE);
      if (users.find(u => u.email === email.toLowerCase())) {
        return send(res, 400, { error: 'Email already registered' });
      }
      const user = {
        id: crypto.randomUUID(),
        email: email.toLowerCase(),
        name: name || email.split('@')[0],
        password: hashPassword(password),
        createdAt: new Date().toISOString()
      };
      users.push(user);
      writeJSON(USERS_FILE, users);
      const token = createToken(user.id);
      return send(res, 201, { token, user: { id: user.id, email: user.email, name: user.name } });
    }

    if (pathname === '/api/login' && method === 'POST') {
      const body = await parseBody(req);
      const { email, password } = body;
      if (!email || !password) return send(res, 400, { error: 'Email and password required' });
      const users = readJSON(USERS_FILE);
      const user = users.find(u => u.email === email.toLowerCase() && u.password === hashPassword(password));
      if (!user) return send(res, 401, { error: 'Invalid email or password' });
      const token = createToken(user.id);
      return send(res, 200, { token, user: { id: user.id, email: user.email, name: user.name } });
    }

    if (pathname === '/api/me' && method === 'GET') {
      const userId = getAuthUser(req);
      if (!userId) return send(res, 401, { error: 'Unauthorized' });
      const users = readJSON(USERS_FILE);
      const user = users.find(u => u.id === userId);
      if (!user) return send(res, 401, { error: 'User not found' });
      return send(res, 200, { id: user.id, email: user.email, name: user.name });
    }

    // Journal routes (protected)
    if (pathname.startsWith('/api/journals')) {
      const userId = getAuthUser(req);
      if (!userId) {
        return send(res, 401, { error: 'Unauthorized' });
      }

      // Get all journals for user (list of dates)
      if (pathname === '/api/journals' && method === 'GET') {
        const all = readJSON(JOURNALS_FILE);
        const userJournals = all[userId] || {};
        const dates = Object.keys(userJournals).sort().reverse();
        return send(res, 200, { dates, journals: userJournals });
      }

      // Get specific journal by date
      if (pathname.startsWith('/api/journals/') && method === 'GET') {
        const date = pathname.split('/').pop();
        const all = readJSON(JOURNALS_FILE);
        const userJournals = all[userId] || {};
        const journal = userJournals[date] || null;
        return send(res, 200, journal || {
          date,
          trades: [],
          startingBalance: 0,
          closedPL: 0,
          totalBrokerage: 0,
          closingBalance: 0,
          profit: 0,
          loss: 0,
          watchlist: '',
          marketMovements: '',
          mistakes: '',
          riskRewards: '',
          notes: ''
        });
      }

      // Create or update journal for a date
      if (pathname.startsWith('/api/journals/') && (method === 'POST' || method === 'PUT')) {
        const date = pathname.split('/').pop();
        const body = await parseBody(req);
        const all = readJSON(JOURNALS_FILE);
        if (!all[userId]) all[userId] = {};
        
        // Calculate totals from trades
        let closedPL = 0, tradeBrokerage = 0, profit = 0, loss = 0;
        const trades = body.trades || [];
        trades.forEach(t => {
          const pl = parseFloat(t.pl) || 0;
          closedPL += pl;
          if (pl > 0) profit += pl;
          else loss += Math.abs(pl);
          if (t.brokerage) tradeBrokerage += parseFloat(t.brokerage) || 0;
        });
        const starting = parseFloat(body.startingBalance) || 0;
        const totalBrokerage = body.totalBrokerage !== undefined ? (parseFloat(body.totalBrokerage) || 0) : tradeBrokerage;
        const closing = starting + closedPL - totalBrokerage;

        const journal = {
          date,
          day: body.day || new Date(date + 'T12:00:00').toLocaleDateString('en-US', { weekday: 'long' }),
          startingBalance: starting,
          closedPL,
          totalBrokerage,
          closingBalance: closing,
          trades,
          profit,
          loss,
          watchlist: body.watchlist || '',
          marketMovements: body.marketMovements || '',
          mistakes: body.mistakes || '',
          riskRewards: body.riskRewards || '',
          notes: body.notes || '',
          updatedAt: new Date().toISOString()
        };
        all[userId][date] = journal;
        writeJSON(JOURNALS_FILE, all);
        return send(res, 200, journal);
      }

      // Delete journal
      if (pathname.startsWith('/api/journals/') && method === 'DELETE') {
        const date = pathname.split('/').pop();
        const all = readJSON(JOURNALS_FILE);
        if (all[userId] && all[userId][date]) {
          delete all[userId][date];
          writeJSON(JOURNALS_FILE, all);
        }
        return send(res, 200, { success: true });
      }
    }

    // If an unknown /api/ route was requested, return 404 JSON
    if (pathname.startsWith('/api/')) {
      return send(res, 404, { error: 'API route not found' });
    }

    // Non-API requests: If frontend directory exists, serve static files; else return API status
    if (!fs.existsSync(FRONTEND_DIR)) {
      if (pathname === '/' || pathname === '/index.html') {
        return send(res, 200, { status: 'ok', message: 'Trade Tracker Backend API is running' });
      }
      return send(res, 404, { error: 'Not found' });
    }

    return serveStatic(req, res, pathname);

  } catch (err) {
    console.error('Server error:', err);
    send(res, 500, { error: err.message || 'Server error' });
  }
}

const server = http.createServer(handleRequest);
server.listen(PORT, () => {
  console.log('====================================================');
  console.log(`Trading Journal App is LIVE on port ${PORT}!`);
  console.log(`Open in browser: http://localhost:${PORT}`);
  console.log(`Demo Account:    demo@tradingjournal.com / password123`);
  console.log('====================================================');
});
