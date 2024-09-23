import React, { useState } from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import MenuIcon from '@mui/icons-material/Menu';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Divider from '@mui/material/Divider';
import SettingsIcon from '@mui/icons-material/Settings';
import InfoIcon from '@mui/icons-material/Info';
import { makeStyles } from '@mui/styles';
import LoginIcon from '@mui/icons-material/Login';

const useStyles = makeStyles({
    appBar: {
      backgroundColor: '#f9f9f9',
      boxShadow: '0px 4px 10px rgba(0, 0, 0, 0.1)',
      padding: '0 20px',
    },
    toolbar: {
      display: 'flex',
      justifyContent: 'space-between',
    },
    rightSection: {
      display: 'flex',
      alignItems: 'center',
    },
    drawerPaper: {
      width: 250,
      backgroundColor: '#f5f5f5',
      boxShadow: '2px 0px 10px rgba(0, 0, 0, 0.1)',
    },
    drawerContent: {
      padding: '20px 10px',
    },
    listItem: {
      borderRadius: '8px',
      '&:hover': {
        backgroundColor: '#e0f7fa',
      },
    },
    loginButton: {
      borderRadius: '20px',
      boxShadow: '0px 2px 5px rgba(0, 0, 0, 0.2)',
      color: '#fff',
      backgroundColor: '#f50057',
      '&:hover': {
        backgroundColor: '#ff4081',
      },
    },
  });

  const Menu = () => {
    const classes = useStyles();
    const [drawerOpen, setDrawerOpen] = useState(false);
  
    const toggleDrawer = (open) => (event) => {
      if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
        return;
      }
      setDrawerOpen(open);
    };
  
    const drawerList = () => (
      <Box
        className={classes.drawerContent}
        onClick={toggleDrawer(false)}
        onKeyDown={toggleDrawer(false)}
      >
        <List>
          <ListItem button className={classes.listItem}>
            <ListItemIcon>
              <SettingsIcon />
            </ListItemIcon>
            <ListItemText primary="Settings" />
          </ListItem>
          <ListItem button className={classes.listItem}>
            <ListItemIcon>
              <InfoIcon />
            </ListItemIcon>
            <ListItemText primary="About" />
          </ListItem>
        </List>
        <Divider />
      </Box>
    );
  
    return (
      <Box sx={{ flexGrow: 1 }} style={{ width: "100%", height: "10vh" }}>
        <AppBar position="static" className={classes.appBar}>
          <Toolbar className={classes.toolbar}>
            {/* Title */}
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
              SecretaryApp
            </Typography>
            <div className={classes.rightSection}>
              {/* Login Button */}
              <Button
                variant="contained"
                startIcon={<LoginIcon />}
                className={classes.loginButton}
              >
                Login
              </Button>
              {/* Drawer Button */}
                <IconButton
                edge="start"
                color="inherit"
                aria-label="menu"
                onClick={toggleDrawer(true)}
                style={{margin: "10px"}}
              >
                <MenuIcon />
              </IconButton>
            </div>
          </Toolbar>
        </AppBar>
        
        <Drawer
          anchor="right"
          open={drawerOpen}
          onClose={toggleDrawer(false)}
          classes={{ paper: classes.drawerPaper }}
        >
          {drawerList()}
        </Drawer>
      </Box>
    );
  };

export default Menu