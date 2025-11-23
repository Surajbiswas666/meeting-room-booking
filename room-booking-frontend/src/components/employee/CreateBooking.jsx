import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { roomAPI, bookingAPI } from '../../services/api';
import Navbar from '../layout/Navbar';
import { Calendar, Clock, Users, FileText, Building2 } from 'lucide-react';
import toast from 'react-hot-toast';

export default function CreateBooking() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    roomId: '',
    meetingTitle: '',
    description: '',
    bookingDate: '',
    startTime: '',
    endTime: '',
    attendeesCount: '',
  });

  useEffect(() => {
    fetchRooms();
  }, []);

  const fetchRooms = async () => {
    try {
      console.log('Fetching rooms...');
      const response = await roomAPI.getAll();
      console.log('Rooms response:', response.data);
      
      if (response.data.success && response.data.data) {
        setRooms(response.data.data);
        console.log('Rooms loaded:', response.data.data.length);
      } else {
        toast.error('No rooms available');
      }
    } catch (error) {
      console.error('Error fetching rooms:', error);
      toast.error('Failed to fetch rooms: ' + (error.message || 'Unknown error'));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const bookingData = {
        ...formData,
        userId: user.userId,
        roomId: parseInt(formData.roomId),
        attendeesCount: parseInt(formData.attendeesCount) || 0,
      };

      await bookingAPI.create(bookingData);
      toast.success('Booking request submitted! Waiting for admin approval.');
      navigate('/employee/my-bookings');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to create booking');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Create New Booking</h1>
          <p className="text-gray-600 dark:text-gray-400 mt-1">Request a meeting room for your event</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Room Selection */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                <Building2 className="inline w-4 h-4 mr-1" />
                Select Room *
              </label>
              <select
                name="roomId"
                value={formData.roomId}
                onChange={handleChange}
                className="input-field"
                required
              >
                <option value="">Choose a room</option>
                {rooms.map((room) => (
                  <option key={room.id} value={room.id}>
                    {room.name} - Capacity: {room.capacity} (Floor {room.floor})
                  </option>
                ))}
              </select>
            </div>

            {/* Meeting Title */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                <FileText className="inline w-4 h-4 mr-1" />
                Meeting Title *
              </label>
              <input
                type="text"
                name="meetingTitle"
                value={formData.meetingTitle}
                onChange={handleChange}
                className="input-field"
                placeholder="e.g., Sprint Planning"
                required
              />
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Description
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                className="input-field"
                rows="3"
                placeholder="Brief description of the meeting"
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Booking Date */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <Calendar className="inline w-4 h-4 mr-1" />
                  Date *
                </label>
                <input
                  type="date"
                  name="bookingDate"
                  value={formData.bookingDate}
                  onChange={handleChange}
                  className="input-field"
                  min={new Date().toISOString().split('T')[0]}
                  required
                />
              </div>

              {/* Attendees Count */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <Users className="inline w-4 h-4 mr-1" />
                  Number of Attendees
                </label>
                <input
                  type="number"
                  name="attendeesCount"
                  value={formData.attendeesCount}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="e.g., 10"
                  min="1"
                />
              </div>

              {/* Start Time */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <Clock className="inline w-4 h-4 mr-1" />
                  Start Time *
                </label>
                <input
                  type="time"
                  name="startTime"
                  value={formData.startTime}
                  onChange={handleChange}
                  className="input-field"
                  required
                />
              </div>

              {/* End Time */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <Clock className="inline w-4 h-4 mr-1" />
                  End Time *
                </label>
                <input
                  type="time"
                  name="endTime"
                  value={formData.endTime}
                  onChange={handleChange}
                  className="input-field"
                  required
                />
              </div>
            </div>

            {/* Info Box */}
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <p className="text-sm text-blue-800">
                <strong>Note:</strong> Your booking request will be sent to the admin for approval. 
                You will receive an email notification once it's reviewed.
              </p>
            </div>

            {/* Submit Buttons */}
            <div className="flex justify-end space-x-4">
              <button
                type="button"
                onClick={() => navigate('/employee/dashboard')}
                className="btn-secondary"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                className="btn-primary disabled:opacity-50"
              >
                {loading ? 'Submitting...' : 'Submit Request'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}