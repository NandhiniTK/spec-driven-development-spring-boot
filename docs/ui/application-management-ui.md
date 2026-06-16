# Application Management UI Specification

## Overview
A simple, single-page React application for managing applications. No authentication required - designed for experimental/demo purposes.

## Technology Stack
- **Framework**: React 18+ with Vite
- **Styling**: Tailwind CSS
- **HTTP Client**: Axios
- **State Management**: React Hooks (useState, useEffect)
- **Notifications**: React Hot Toast / React Toastify
- **Build Tool**: Vite

## Page Layout

### Single Page Application
```
┌─────────────────────────────────────────────────────────┐
│  Header: "Application Management"                       │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Create Application Form                                │
│  ┌────────────────────────────────────────────────┐    │
│  │ Name:         [________________]               │    │
│  │ Description:  [________________]               │    │
│  │               [________________]               │    │
│  │ Customer ID:  [________________]               │    │
│  │                                                 │    │
│  │ [Submit]  [Clear]                              │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Applications Table                    [Refresh]        │
│  ┌────────────────────────────────────────────────┐    │
│  │ ID | Name | Description | Customer | Created  │    │
│  ├────────────────────────────────────────────────┤    │
│  │ 1  | App1 | Test app    | CUST001  | 2026-... │    │
│  │ 2  | App2 | Demo app    | CUST002  | 2026-... │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## Components Structure

### 1. App.jsx (Main Component)
```
src/
├── App.jsx                 # Main application component
├── components/
│   ├── ApplicationForm.jsx # Form to create applications
│   ├── ApplicationTable.jsx # Table to display applications
│   └── Header.jsx          # Page header
├── services/
│   └── applicationService.js # API service layer
├── utils/
│   └── dateFormatter.js    # Date formatting utilities
└── styles/
    └── index.css           # Global styles + Tailwind
```

## Component Specifications

### ApplicationForm Component

**Props**: None

**State**:
- `formData`: { name: '', description: '', customerId: '' }
- `loading`: boolean
- `errors`: { name: '', description: '', customerId: '' }

**Features**:
- Input validation (all fields required)
- Submit button disabled during API call
- Auto-clear form after successful submission
- Error handling with toast notifications

**API Call**:
```javascript
POST http://localhost:8080/api/applications
Content-Type: application/json

{
  "name": "My Application",
  "description": "Application description",
  "customerId": "CUST001"
}
```

**Validation Rules**:
- Name: Required, min 3 characters, max 100 characters
- Description: Required, min 10 characters, max 500 characters
- Customer ID: Required, alphanumeric, max 50 characters

---

### ApplicationTable Component

**Props**: 
- `refreshTrigger`: number (to trigger refresh from parent)

**State**:
- `applications`: array
- `loading`: boolean
- `error`: string | null

**Features**:
- Fetch all applications on mount
- Refresh button to reload data
- Loading spinner during fetch
- Empty state message when no applications
- Responsive table design

**API Call**:
```javascript
GET http://localhost:8080/api/applications
```

**Table Columns**:
1. **ID** - Application ID (UUID, truncated)
2. **Name** - Application name
3. **Description** - Application description (truncated if long)
4. **Customer ID** - Customer identifier
5. **Created Date** - Formatted date (e.g., "Jan 15, 2026, 10:30 AM")
6. **Updated Date** - Formatted date

---

### Header Component

**Props**: None

**Features**:
- Display application title
- Simple, clean design
- Responsive

---

## API Service Layer

### applicationService.js

```javascript
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const applicationService = {
  // Create new application
  createApplication: async (applicationData) => {
    const response = await axios.post(
      `${API_BASE_URL}/api/applications`,
      applicationData
    );
    return response.data;
  },

  // Get all applications
  getAllApplications: async () => {
    const response = await axios.get(`${API_BASE_URL}/api/applications`);
    return response.data;
  }
};
```

---

## User Flows

### Flow 1: Create Application

1. User fills in the form (Name, Description, Customer ID)
2. User clicks "Submit"
3. Form validates inputs
4. If valid:
   - Submit button shows loading state
   - API call to POST /api/applications
   - On success:
     - Show success toast: "Application created successfully!"
     - Clear form
     - Refresh applications table
   - On error:
     - Show error toast with error message
5. If invalid:
   - Show validation errors below fields

### Flow 2: View Applications

1. Page loads
2. Fetch all applications from API
3. Display in table
4. If no applications:
   - Show empty state: "No applications found. Create your first application!"
5. If error:
   - Show error message

### Flow 3: Refresh Applications

1. User clicks "Refresh" button
2. Show loading spinner
3. Fetch all applications from API
4. Update table with new data

---

## Styling Guidelines

### Color Scheme
- **Primary**: Blue (#3B82F6)
- **Success**: Green (#10B981)
- **Error**: Red (#EF4444)
- **Background**: White (#FFFFFF)
- **Text**: Gray-900 (#111827)
- **Border**: Gray-300 (#D1D5DB)

### Tailwind Classes

**Form**:
```jsx
<input className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
```

**Button**:
```jsx
<button className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed">
  Submit
</button>
```

**Table**:
```jsx
<table className="min-w-full divide-y divide-gray-200">
  <thead className="bg-gray-50">
    <tr>
      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
        Name
      </th>
    </tr>
  </thead>
</table>
```

---

## Error Handling

### API Errors
- **Network Error**: "Unable to connect to server. Please check your connection."
- **400 Bad Request**: Display validation errors from API
- **500 Server Error**: "Server error. Please try again later."
- **Timeout**: "Request timeout. Please try again."

### Form Validation Errors
- Display inline below each field
- Red text color
- Clear on field change

---

## Loading States

### Form Submission
- Disable submit button
- Show spinner icon in button
- Change button text to "Creating..."

### Table Loading
- Show skeleton loader or spinner
- Disable refresh button

---

## Responsive Design

### Mobile (< 768px)
- Stack form fields vertically
- Horizontal scroll for table
- Smaller padding and font sizes

### Tablet (768px - 1024px)
- Two-column form layout
- Full-width table

### Desktop (> 1024px)
- Centered layout with max-width
- Comfortable spacing
- Full table visible

---

## Environment Configuration

### .env file
```
VITE_API_BASE_URL=http://localhost:8080
```

### .env.production
```
VITE_API_BASE_URL=https://api.production.com
```

---

## Build & Deployment

### Development
```bash
npm run dev
# Runs on http://localhost:5173
```

### Production Build
```bash
npm run build
# Creates optimized build in dist/
```

### Preview Production Build
```bash
npm run preview
```

---

## Testing Checklist

- [ ] Form validation works for all fields
- [ ] Create application API call succeeds
- [ ] Success toast appears after creation
- [ ] Form clears after successful submission
- [ ] Table loads applications on mount
- [ ] Refresh button updates table
- [ ] Error handling works for API failures
- [ ] Responsive design works on mobile/tablet/desktop
- [ ] Loading states display correctly
- [ ] Empty state shows when no applications

---

## Future Enhancements (Not in Scope)

- Pagination for large datasets
- Search and filter functionality
- Edit/Delete application
- Application details modal
- Authentication and authorization
- Dark mode
- Export to CSV
