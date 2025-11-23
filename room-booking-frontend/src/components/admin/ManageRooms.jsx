import { useState, useEffect } from 'react';
import { roomAPI } from '../../services/api';
import Navbar from '../layout/Navbar';
import { Building2, Plus, Edit2, Trash2, Users, MapPin } from 'lucide-react';
import toast from 'react-hot-toast';

export default function ManageRooms() {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingRoom, setEditingRoom] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    capacity: '',
    floor: '',
    amenities: '',
  });

  useEffect(() => {
    fetchRooms();
  }, []);

  const fetchRooms = async () => {
    try {
      const response = await roomAPI.getAll();
      setRooms(response.data.data);
    } catch (error) {
      toast.error('Failed to fetch rooms');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      if (editingRoom) {
        await roomAPI.update(editingRoom.id, formData);
        toast.success('Room updated successfully');
      } else {
        await roomAPI.create(formData);
        toast.success('Room created successfully');
      }
      
      setShowModal(false);
      resetForm();
      fetchRooms();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Operation failed');
    }
  };

  const handleEdit = (room) => {
    setEditingRoom(room);
    setFormData({
      name: room.name,
      capacity: room.capacity,
      floor: room.floor || '',
      amenities: room.amenities || '',
    });
    setShowModal(true);
  };

  const handleDelete = async (roomId) => {
    if (!window.confirm('Are you sure you want to delete this room?')) return;

    try {
      await roomAPI.delete(roomId);
      toast.success('Room deleted successfully');
      fetchRooms();
    } catch (error) {
      toast.error('Failed to delete room');
    }
  };

  const resetForm = () => {
    setFormData({ name: '', capacity: '', floor: '', amenities: '' });
    setEditingRoom(null);
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Manage Rooms</h1>
            <p className="text-gray-600 dark:text-gray-400 mt-1">Add, edit, or remove meeting rooms</p>
          </div>
          <button
            onClick={() => { resetForm(); setShowModal(true); }}
            className="btn-primary flex items-center space-x-2"
          >
            <Plus className="w-5 h-5" />
            <span>Add Room</span>
          </button>
        </div>

        {loading ? (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {rooms.map((room) => (
              <div key={room.id} className="card hover:shadow-xl transition-shadow">
                <div className="h-40 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-lg mb-4 flex items-center justify-center">
                  <Building2 className="w-20 h-20 text-white opacity-50" />
                </div>

                <h3 className="text-xl font-bold text-gray-900 mb-3">{room.name}</h3>

                <div className="space-y-2 mb-4">
                  <div className="flex items-center text-gray-600">
                    <Users className="w-5 h-5 mr-2" />
                    <span>Capacity: {room.capacity}</span>
                  </div>
                  <div className="flex items-center text-gray-600">
                    <MapPin className="w-5 h-5 mr-2" />
                    <span>Floor {room.floor}</span>
                  </div>
                </div>

                {room.amenities && (
                  <div className="mb-4">
                    <p className="text-sm font-medium text-gray-700 mb-2">Amenities:</p>
                    <div className="flex flex-wrap gap-2">
                      {room.amenities.split(',').map((amenity, index) => (
                        <span
                          key={index}
                          className="px-2 py-1 bg-blue-50 text-blue-600 text-xs rounded-full"
                        >
                          {amenity.trim()}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                <div className="flex space-x-2">
                  <button
                    onClick={() => handleEdit(room)}
                    className="flex-1 flex items-center justify-center space-x-1 px-4 py-2 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition-colors"
                  >
                    <Edit2 className="w-4 h-4" />
                    <span>Edit</span>
                  </button>
                  <button
                    onClick={() => handleDelete(room.id)}
                    className="flex-1 flex items-center justify-center space-x-1 px-4 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors"
                  >
                    <Trash2 className="w-4 h-4" />
                    <span>Delete</span>
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Add/Edit Modal */}
        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-md w-full p-6">
              <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
                {editingRoom ? 'Edit Room' : 'Add New Room'}
              </h2>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Room Name *
                  </label>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    className="input-field"
                    placeholder="e.g., Conference Room A"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Capacity *
                  </label>
                  <input
                    type="number"
                    name="capacity"
                    value={formData.capacity}
                    onChange={handleChange}
                    className="input-field"
                    placeholder="e.g., 10"
                    min="1"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Floor
                  </label>
                  <input
                    type="number"
                    name="floor"
                    value={formData.floor}
                    onChange={handleChange}
                    className="input-field"
                    placeholder="e.g., 2"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Amenities
                  </label>
                  <input
                    type="text"
                    name="amenities"
                    value={formData.amenities}
                    onChange={handleChange}
                    className="input-field"
                    placeholder="e.g., Projector,Whiteboard,TV"
                  />
                  <p className="text-xs text-gray-500 mt-1">Separate with commas</p>
                </div>

                <div className="flex space-x-3 pt-4">
                  <button
                    type="button"
                    onClick={() => { setShowModal(false); resetForm(); }}
                    className="flex-1 btn-secondary"
                  >
                    Cancel
                  </button>
                  <button type="submit" className="flex-1 btn-primary">
                    {editingRoom ? 'Update' : 'Create'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}