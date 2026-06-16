# Application Management UI

A React-based user interface for managing applications. Built with Vite, Tailwind CSS, and Axios.

## Features

- ✨ Create new applications with validation
- 📋 View all applications in a responsive table
- 🔄 Auto-refresh after creating applications
- 🎨 Modern UI with Tailwind CSS
- 🔔 Toast notifications for success/error messages
- 📱 Fully responsive design

## Tech Stack

- **React** 18.2.0
- **Vite** 5.0.8 - Fast build tool
- **Tailwind CSS** 3.4.0 - Utility-first CSS
- **Axios** 1.6.0 - HTTP client
- **React Hot Toast** 2.4.1 - Toast notifications

## Prerequisites

- Node.js 18+ LTS
- npm or yarn
- Application Management Service running on `http://localhost:8080`

## Setup & Installation

1. **Install dependencies**
   ```bash
   npm install
   ```

2. **Configure environment variables**
   
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
   
   The default configuration:
   ```
   VITE_API_BASE_URL=http://localhost:8080
   ```

3. **Start development server**
   ```bash
   npm run dev
   ```
   
   The application will open at `http://localhost:5173`

## Available Scripts

- `npm run dev` - Start development server with HMR
- `npm run build` - Build for production
- `npm run preview` - Preview production build locally

## Project Structure

```
src/
├── components/
│   ├── Header.jsx              # Page header component
│   ├── ApplicationForm.jsx     # Form to create applications
│   └── ApplicationTable.jsx    # Table to display applications
├── services/
│   └── applicationService.js   # API service layer
├── App.jsx                     # Main application component
├── main.jsx                    # Application entry point
└── index.css                   # Global styles with Tailwind
```

## Component Details

### ApplicationForm
- **Fields**: Name, Description, Customer ID
- **Validation**: 
  - Name: 3-100 characters
  - Description: 10-500 characters
  - Customer ID: Alphanumeric, max 50 characters
- **Features**: Auto-clear on success, loading states, error handling

### ApplicationTable
- **Columns**: ID, Name, Description, Customer ID, Created Date, Updated Date
- **Features**: Refresh button, loading spinner, empty state, responsive design
- **Data Mapping**: Displays `owner` field as "Customer ID"

## API Integration

The UI communicates with the Application Management Service:

- **POST** `/api/v1/applications` - Create application
- **GET** `/api/v1/applications` - Get all applications

### Field Mapping

UI `customerId` → API `owner` field

The service layer automatically maps the UI field to the correct API field.

## Usage

1. **Create Application**
   - Fill in the form (all fields required)
   - Click "Submit"
   - Success toast appears
   - Form clears automatically
   - Table refreshes with new application

2. **View Applications**
   - Applications load automatically on page load
   - Click "Refresh" to reload data
   - Hover over description to see full text

## Styling

Built with Tailwind CSS utility classes:
- **Primary Color**: Blue (#3B82F6)
- **Success**: Green (#10B981)
- **Error**: Red (#EF4444)
- **Background**: Gray-50 (#F9FAFB)

## Error Handling

- **Form Validation**: Inline error messages
- **API Errors**: Toast notifications with error details
- **Network Errors**: User-friendly error messages
- **Loading States**: Spinners and disabled buttons

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Development Notes

- Hot Module Replacement (HMR) enabled
- React StrictMode enabled for development
- Tailwind CSS warnings in IDE are expected (resolved after npm install)

## Troubleshooting

**Port 5173 already in use:**
```bash
# Kill the process using port 5173
netstat -ano | findstr :5173
taskkill /PID <process-id> /F
```

**Backend connection failed:**
- Verify Application Management Service is running on port 8080
- Check CORS configuration in backend
- Verify `.env` file has correct API URL

**Build errors:**
```bash
# Clean install
rm -rf node_modules package-lock.json
npm install
```

## Production Build

```bash
npm run build
```

Output will be in `dist/` directory. Serve with any static file server.

## License

Part of the Spec-Driven Development POC project.
